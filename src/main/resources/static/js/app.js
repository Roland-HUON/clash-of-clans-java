import { api, ApiError } from './api.js';
import { VillageScene } from './village.js';
import { DARK_ELIXIR, ELIXIR, GOLD, RESOURCE_ICON, RESOURCE_LABEL, TROPHY } from './icons.js';

const $ = (id) => document.getElementById(id);

const el = {
  status: $('status'),
  playerSelect: $('playerSelect'),
  gold: $('resGold'),
  elixir: $('resElixir'),
  dark: $('resDark'),
  trophies: $('resTrophies'),
  rank: $('playerRank'),
  leaderboard: $('leaderboard'),
  villageSelect: $('villageSelect'),
  buildingList: $('buildingList'),
  villageCardTitle: $('villageCardTitle'),
  pendingLoot: $('pendingLoot'),
  collectBtn: $('collectBtn'),
  collectRow: document.querySelector('.collect'),
  buildType: $('buildType'),
  buildBtn: $('buildBtn'),
  buildHint: $('buildHint'),
  villageTitle: $('villageTitle'),
  homeBtn: $('homeBtn'),
  tooltip: $('tooltip'),
  troopList: $('troopList'),
  troopHint: $('troopHint'),
  targetSelect: $('targetSelect'),
  campFill: $('campFill'),
  campLabel: $('campLabel'),
  camp: document.querySelector('.camp'),
  armyList: $('armyList'),
  attackBtn: $('attackBtn'),
  raidHint: $('raidHint'),
  overlay: $('battleOverlay'),
  overlayTitle: $('overlayTitle'),
  stars: $('stars'),
  ovDestruction: $('ovDestruction'),
  ovTurns: $('ovTurns'),
  ovSurvivors: $('ovSurvivors'),
  ovLoot: $('ovLoot'),
  targetLoot: $('targetLoot'),
  overlayClose: $('overlayClose'),
  toast: $('toast')
};

const state = {
  players: [],
  troopTypes: new Map(),
  buildingTypes: new Map(),
  player: null,
  homeVillage: null,
  shownVillage: null,
  scouting: false,
  unlocked: [],
  army: new Map(),
  targets: [],
  rank: null,
  busy: false
};

const scene = new VillageScene($('canvasHost'));
const format = (n) => new Intl.NumberFormat('en').format(n ?? 0);
const LEADERBOARD_SIZE = 25;
const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (c) =>
  ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const compact = (n) => (Math.abs(n ?? 0) < 100_000
  ? format(n)
  : new Intl.NumberFormat('en', { notation: 'compact', maximumFractionDigits: 1 }).format(n));
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
const balanceKey = { GOLD: 'gold', ELIXIR: 'elixir', DARK_ELIXIR: 'darkElixir' };

for (const [id, icon] of [['hudGold', GOLD], ['hudElixir', ELIXIR], ['hudDark', DARK_ELIXIR], ['hudTrophy', TROPHY]]) {
  $(id).insertAdjacentHTML('afterbegin', icon);
}

let toastTimer = null;
let viewToken = 0;

function setStatus(text, kind = '') {
  el.status.textContent = text;
  el.status.className = `status ${kind}`;
}

function toast(message, kind = '') {
  el.toast.textContent = message;
  el.toast.className = `toast ${kind}`;
  el.toast.removeAttribute('hidden');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.toast.setAttribute('hidden', ''), 3200);
}

function describe(error) {
  if (error instanceof ApiError) {
    if (error.status === 429) return 'Rate limit reached — wait a minute.';
    return error.message;
  }
  return error.message || 'Unreachable API';
}

function familyOf(type) {
  if (type.defensive) return 'def';
  if (type.resource) return 'res';
  return 'core';
}

scene.onHover = (hit) => {
  if (!hit) return el.tooltip.setAttribute('hidden', '');

  const building = hit.building;
  const type = state.buildingTypes.get(building.type) || {};
  const blast = type.splashTargets > 0 ? `, splash +${type.splashTargets}` : '';
  const reach = type.defensive
    ? `fires at ${String(type.targets).replace('_', ' ').toLowerCase()}${blast}`
    : type.resource ? 'resource building' : type.camp ? 'holds troops' : 'not a defence';

  el.tooltip.innerHTML =
    `${escapeHtml(building.label)} <small>level ${building.level}/${building.maxLevel} · ${format(building.hitPoints)} hp · ${reach}</small>`;
  el.tooltip.removeAttribute('hidden');

  const box = el.tooltip.parentElement.getBoundingClientRect();
  el.tooltip.style.left = `${hit.x - box.left}px`;
  el.tooltip.style.top = `${hit.y - box.top}px`;
};

async function boot() {
  try {
    setStatus('loading…');

    const [troopTypes, buildingTypes, players] = await Promise.all([
      api.troopTypes(), api.buildingTypes(), api.players()
    ]);

    troopTypes.forEach((t) => state.troopTypes.set(t.id, t));
    buildingTypes.forEach((t) => state.buildingTypes.set(t.id, t));
    state.players = players;

    el.playerSelect.innerHTML = players.map((p) => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join('');

    await loadTargets();
    await selectPlayer(players[0].id);

    setStatus('connected', 'ok');
  } catch (error) {
    setStatus(describe(error), 'err');
    toast(describe(error), 'err');
  }
}

async function loadTargets() {
  const byId = new Map(state.players.map((player) => [player.id, player]));
  const villages = await api.allVillages();
  state.targets = villages.map((village) => ({ ...village, owner: byId.get(village.playerId) }));
}

async function selectPlayer(playerId) {
  const token = ++viewToken;
  const [player, villages, troops, rank, leaderboard] = await Promise.all([
    api.player(playerId), api.villages(playerId), api.troops(playerId),
    api.rank(playerId), api.leaderboard(LEADERBOARD_SIZE)
  ]);

  if (token !== viewToken) return;

  state.player = player;
  state.unlocked = troops;

  renderHud(player, rank);
  renderLeaderboard(leaderboard, player);

  el.villageSelect.innerHTML = villages.map((v) => `<option value="${v.id}">${escapeHtml(v.name)}</option>`).join('');

  renderTroops();
  renderArmyPicker();
  renderTargets();

  if (villages.length) {
    await showVillage(villages[0].id, { home: true });
  } else {
    state.homeVillage = null;
    el.villageTitle.textContent = `${player.name} has no village yet`;
    el.buildingList.innerHTML = '';
    el.collectRow.hidden = true;
    el.buildBtn.disabled = true;
    scene.setVillage({ buildings: [] }, state.buildingTypes);
  }
}

function renderHud(player, rank) {
  if (rank) state.rank = rank;
  rank = state.rank || rank;
  for (const [node, value] of [[el.gold, player.gold], [el.elixir, player.elixir], [el.dark, player.darkElixir]]) {
    node.textContent = compact(value);
    node.parentElement.title = `${format(value)}`;
  }
  el.trophies.textContent = format(player.trophies);
  el.rank.textContent = rank ? `Rank ${rank.rank}` : '—';
}

function renderLeaderboard(entries, player) {
  el.leaderboard.innerHTML = entries.map((entry) =>
    `<li class="${entry.playerId === player.id ? 'me' : ''}" data-player="${entry.playerId}">
       ${escapeHtml(entry.name)}<span>${format(entry.trophies)}</span>
     </li>`).join('');

  for (const row of el.leaderboard.querySelectorAll('li')) {
    row.addEventListener('click', () => scoutPlayer(Number(row.dataset.player)));
  }
}

async function showVillage(villageId, { home = false } = {}) {
  const token = ++viewToken;
  const village = await api.village(villageId);
  if (token !== viewToken) return;

  state.shownVillage = village;
  state.scouting = !home;
  if (home) state.homeVillage = village;

  present(village, home ? 'home' : 'scout');
  scene.resetCamera();
  updateCamp();
}

function present(village, mode) {
  const editable = mode === 'home';
  state.scouting = !editable;
  const owner = state.players.find((p) => p.id === village.playerId);
  const name = owner ? owner.name : 'Enemy';

  el.villageTitle.textContent =
      mode === 'home' ? `${village.name} — your village`
    : mode === 'raid' ? `Raiding ${name} — ${village.name}`
    : `Scouting ${name} — ${village.name}`;

  el.villageTitle.classList.toggle('under-attack', mode === 'raid');
  el.homeBtn.hidden = mode !== 'scout';

  el.villageCardTitle.textContent = editable ? 'Village' : `${name}'s village`;
  el.villageSelect.hidden = !editable;
  el.rank.hidden = !editable;

  scene.setVillage(village, state.buildingTypes);
  renderBuildings(village, editable);
  renderBuildOptions(village, editable);
  renderPending(village, editable);
  renderTroops(editable);
}

function renderPending(village, editable) {
  const pending = village.pending || { gold: 0, elixir: 0, darkElixir: 0 };
  const total = pending.gold + pending.elixir + pending.darkElixir;

  el.collectRow.hidden = !editable;
  if (!editable) return;

  el.pendingLoot.innerHTML = [[GOLD, pending.gold], [ELIXIR, pending.elixir], [DARK_ELIXIR, pending.darkElixir]]
    .map(([icon, value]) => `<li${value === 0 ? ' class="zero"' : ''}>${icon}${format(value)}</li>`)
    .join('');

  el.collectBtn.disabled = total === 0;
  el.collectBtn.title = total === 0
    ? 'Your mines are empty — come back later.'
    : 'Collect what the mines have produced';
}

async function collect() {
  if (state.busy || !state.homeVillage) return;
  state.busy = true;
  try {
    const harvest = await api.collect(state.homeVillage.id);
    const total = harvest.gold + harvest.elixir + harvest.darkElixir;

    toast(total === 0
      ? 'The mines had nothing yet.'
      : `Collected ${format(harvest.gold)} gold, ${format(harvest.elixir)} elixir, ${format(harvest.darkElixir)} dark elixir`);

    await refreshAfterSpending({ village: true });
  } catch (error) {
    toast(describe(error), 'err');
  } finally {
    state.busy = false;
  }
}

function renderBuildings(village, editable) {
  el.buildingList.innerHTML = village.buildings.map((building) => {
    const type = state.buildingTypes.get(building.type) || {};
    return `<li data-id="${building.id}">
      <i class="dot ${familyOf(type)}"></i>
      <span class="row-name">
        <b>${escapeHtml(building.label)}</b>
        <small>level ${building.level}/${building.maxLevel} · ${format(building.hitPoints)} hp</small>
      </span>
      ${upgradeButton(building, editable, 'building')}
    </li>`;
  }).join('');

  for (const row of el.buildingList.querySelectorAll('li')) {
    const id = Number(row.dataset.id);
    row.addEventListener('mouseenter', () => scene.highlight(id));
    row.addEventListener('mouseleave', () => scene.highlight(null));
  }

  bindUpgrades(el.buildingList, upgradeBuilding);
}

function upgradeButton(entity, editable, kind) {
  if (entity.nextUpgradeCost === null || entity.nextUpgradeCost === undefined) {
    return '<button class="up maxed" disabled>MAX</button>';
  }

  const icon = RESOURCE_ICON[entity.upgradeResource];
  if (!editable) {
    return `<button class="up" disabled>${icon}${format(entity.nextUpgradeCost)}</button>`;
  }

  const balance = state.player[balanceKey[entity.upgradeResource]] ?? 0;
  const affordable = balance >= entity.nextUpgradeCost;

  return `<button class="up" data-${kind}="${entity.id}" ${affordable ? '' : 'disabled'}
            title="${affordable ? 'Upgrade' : 'Not enough ' + RESOURCE_LABEL[entity.upgradeResource]}">
            ${icon}${format(entity.nextUpgradeCost)}
          </button>`;
}

function bindUpgrades(root, handler) {
  for (const button of root.querySelectorAll('button.up:not([disabled])')) {
    button.addEventListener('click', (event) => {
      event.stopPropagation();
      handler(Number(button.dataset.building ?? button.dataset.troop));
    });
  }
}

async function upgradeBuilding(id) {
  if (state.busy) return;
  state.busy = true;
  try {
    const upgraded = await api.upgradeBuilding(id);
    toast(`${upgraded.label} is now level ${upgraded.level}`);
    await refreshAfterSpending({ village: true });
  } catch (error) {
    toast(describe(error), 'err');
  } finally {
    state.busy = false;
  }
}

async function upgradeTroop(id) {
  if (state.busy) return;
  state.busy = true;
  try {
    const upgraded = await api.upgradeTroop(id);
    toast(`${upgraded.label} is now level ${upgraded.level}`);
    await refreshAfterSpending({ troops: true });
  } catch (error) {
    toast(describe(error), 'err');
  } finally {
    state.busy = false;
  }
}

async function refreshAfterSpending({ troops = false, village = false } = {}) {
  const calls = [api.player(state.player.id)];
  if (troops) calls.push(api.troops(state.player.id));

  const [player, unlocked] = await Promise.all(calls);

  state.player = player;
  if (troops) state.unlocked = unlocked;

  renderHud(player, state.rank);
  renderTroops(true);
  if (village && state.homeVillage) await showVillage(state.homeVillage.id, { home: true });
}

async function refreshEverything() {
  const [player, troops, rank, leaderboard] = await Promise.all([
    api.player(state.player.id), api.troops(state.player.id),
    api.rank(state.player.id), api.leaderboard(LEADERBOARD_SIZE)
  ]);

  state.player = player;
  state.unlocked = troops;
  state.rank = rank;

  renderHud(player, rank);
  renderLeaderboard(leaderboard, player);
  renderTroops(true);

  if (state.homeVillage) await showVillage(state.homeVillage.id, { home: true });
}

function renderBuildOptions(village, editable) {
  const counts = new Map();
  for (const building of village.buildings) {
    counts.set(building.type, (counts.get(building.type) || 0) + 1);
  }

  const available = [...state.buildingTypes.values()]
    .filter((type) => (counts.get(type.id) || 0) < type.maxCount)
    .sort((a, b) => a.label.localeCompare(b.label));

  el.buildType.innerHTML = available
    .map((type) => `<option value="${type.id}">${escapeHtml(type.label)} (${counts.get(type.id) || 0}/${type.maxCount}) — ${format(type.buildCost)} ${RESOURCE_LABEL[type.upgradeResource]}</option>`)
    .join('');

  el.buildBtn.disabled = !editable || available.length === 0;
  el.buildHint.className = 'hint';
  el.buildHint.textContent = editable
    ? (available.length ? '' : 'Every building is already at its maximum count.')
    : 'You can only build in your own village.';

  if (editable && available.length) updateBuildAffordability();
}

function updateBuildAffordability() {
  const type = state.buildingTypes.get(el.buildType.value);
  if (!type) return;

  const balance = state.player[balanceKey[type.upgradeResource]] ?? 0;
  const affordable = balance >= type.buildCost;

  el.buildBtn.disabled = state.scouting || !affordable;
  el.buildHint.className = affordable ? 'hint' : 'hint err';
  el.buildHint.textContent = affordable
    ? `Costs ${format(type.buildCost)} ${RESOURCE_LABEL[type.upgradeResource]}.`
    : `Not enough ${RESOURCE_LABEL[type.upgradeResource]}: ${format(balance)} of ${format(type.buildCost)}.`;
}

async function build() {
  if (state.busy || !state.homeVillage) return;
  state.busy = true;
  try {
    const created = await api.addBuilding(state.homeVillage.id, { type: el.buildType.value, level: 1 });
    toast(`${created.label} built`);
    await showVillage(state.homeVillage.id, { home: true });
  } catch (error) {
    el.buildHint.className = 'hint err';
    el.buildHint.textContent = describe(error);
    toast(describe(error), 'err');
  } finally {
    state.busy = false;
  }
}

function renderTroops(editable = !state.scouting) {
  el.troopList.innerHTML = state.unlocked.map((troop) => {
    const type = state.troopTypes.get(troop.type) || {};
    const tag = type.role === 'SUPPORT' ? 'support' : type.movement === 'AIR' ? 'air' : 'ground';
    return `<li>
      <i class="dot ${tag}"></i>
      <span class="row-name">
        <b>${escapeHtml(troop.label)}</b>
        <small>level ${troop.level}/${troop.maxLevel} · ${format(troop.hitPoints)} hp · ${troop.damage} dps</small>
      </span>
      ${upgradeButton(troop, editable, 'troop')}
    </li>`;
  }).join('');

  bindUpgrades(el.troopList, upgradeTroop);
  el.troopHint.textContent = 'Upgrading a troop raises it for every raid.';
}

function renderTargets() {
  const options = state.targets.filter((v) => v.playerId !== state.player.id);
  el.targetSelect.innerHTML = options
    .map((v) => `<option value="${v.id}">${escapeHtml(v.owner ? v.owner.name : '?')} — ${escapeHtml(v.name)}</option>`).join('');
  if (!options.length) el.raidHint.textContent = 'No other village to raid.';
  renderTargetLoot();
}

function renderTargetLoot() {
  const target = state.targets.find((v) => v.id === Number(el.targetSelect.value));

  if (!target) {
    el.targetLoot.innerHTML = '<li class="empty">no target</li>';
    return;
  }

  const stock = [[GOLD, target.gold], [ELIXIR, target.elixir], [DARK_ELIXIR, target.darkElixir]];
  const empty = stock.every(([, value]) => value === 0);

  el.targetLoot.innerHTML = stock
    .map(([icon, value]) => `<li${value === 0 ? ' class="zero"' : ''}>${icon}${format(value)}</li>`)
    .join('');

  el.raidHint.className = 'hint';
  el.raidHint.textContent = empty
    ? 'This village has already been stripped bare — you can still take trophies.'
    : '';
}

function renderArmyPicker() {
  state.army.clear();
  el.armyList.innerHTML = state.unlocked.map((troop) => {
    const type = state.troopTypes.get(troop.type) || {};
    const tag = type.role === 'SUPPORT' ? 'support' : type.movement === 'AIR' ? 'air' : 'ground';
    return `<li>
      <i class="dot ${tag}"></i>
      <span class="row-name">
        <b>${escapeHtml(troop.label)}</b>
        <small>level ${troop.level} · ${type.housingSpace} space</small>
      </span>
      <input type="number" min="0" value="0" data-type="${troop.type}">
    </li>`;
  }).join('');

  for (const input of el.armyList.querySelectorAll('input')) {
    input.addEventListener('input', () => {
      const count = Math.max(0, Number(input.value) || 0);
      if (count === 0) state.army.delete(input.dataset.type);
      else state.army.set(input.dataset.type, count);
      updateCamp();
    });
  }
}

function campCapacity() {
  const village = state.homeVillage;
  if (!village) return 0;
  return village.buildings.reduce((sum, b) => sum + (b.housingCapacity || 0), 0);
}

function armyHousing() {
  let used = 0;
  for (const [type, count] of state.army) {
    used += (state.troopTypes.get(type)?.housingSpace || 0) * count;
  }
  return used;
}

function updateCamp() {
  const used = armyHousing();
  const capacity = campCapacity();
  const over = used > capacity;

  el.campFill.style.width = `${capacity ? Math.min(100, (used / capacity) * 100) : 0}%`;
  el.campLabel.textContent = `${used} / ${capacity}`;
  el.camp.classList.toggle('over', over);

  el.attackBtn.disabled = state.busy || used === 0 || over || !el.targetSelect.value;
  el.raidHint.className = 'hint';
  el.raidHint.textContent = over ? 'Army too large for your camps.' : '';
}

async function attack() {
  if (state.busy) return;
  state.busy = true;
  el.attackBtn.disabled = true;
  el.raidHint.className = 'hint';
  el.raidHint.textContent = 'Deploying…';

  const targetId = Number(el.targetSelect.value);
  const target = state.targets.find((v) => v.id === targetId);
  const army = [...state.army].map(([type, count]) => ({ type, count }));

  try {
    const targetVillage = await api.village(targetId);
    const result = await api.raid({
      attackerId: state.player.id,
      attackerVillageId: state.homeVillage.id,
      targetVillageId: targetId,
      army
    });

    await playBattle(targetVillage, target, army, result);
    showResult(result, army);

    await loadTargets();
    await refreshEverything();
    renderTargets();
  } catch (error) {
    el.raidHint.className = 'hint err';
    el.raidHint.textContent = describe(error);
    toast(describe(error), 'err');
  } finally {
    state.busy = false;
    updateCamp();
  }
}

async function playBattle(targetVillage, target, army, result) {
  present(targetVillage, 'raid');

  const units = [];
  for (const { type, count } of army) {
    const troopType = state.troopTypes.get(type);
    for (let i = 0; i < count; i++) units.push({ type, movement: troopType?.movement });
  }

  scene.spawnArmy(units);
  await sleep(600);
  scene.marchTo();
  await sleep(1100);

  const toDestroy = Math.round((result.destructionPercentage / 100) * targetVillage.buildings.length);
  const casualties = units.length - result.survivingTroops;

  const order = [...targetVillage.buildings].sort((a, b) => {
    const ta = state.buildingTypes.get(a.type) || {};
    const tb = state.buildingTypes.get(b.type) || {};
    return (tb.defensive === true) - (ta.defensive === true);
  });

  const steps = Math.max(toDestroy, casualties, 1);
  const stepDelay = Math.min(360, Math.max(140, 4200 / steps));

  for (let step = 0; step < steps; step++) {
    if (step < toDestroy) {
      scene.setHealth(order[step].id, 0);
      scene.destroyBuilding(order[step].id);
    }
    const killed = Math.round((casualties * (step + 1)) / steps) - Math.round((casualties * step) / steps);
    scene.killTroop(killed);

    await sleep(stepDelay);
  }

  await sleep(500);
}

function showResult(result, army) {
  const sent = army.reduce((sum, unit) => sum + unit.count, 0);

  el.overlayTitle.textContent = result.villageDestroyed ? 'Village razed'
    : result.stars > 0 ? 'Victory' : 'Defeat';
  el.ovDestruction.textContent = result.destructionPercentage;
  el.ovTurns.textContent = result.turns;
  el.ovSurvivors.textContent = `${result.survivingTroops}/${sent}`;

  [...el.stars.children].forEach((star, index) => star.classList.toggle('on', index < result.stars));

  const trophy = result.trophyChange >= 0 ? `+${result.trophyChange}` : `${result.trophyChange}`;
  const spoils = [
    [GOLD, format(result.lootedGold), result.lootedGold === 0],
    [ELIXIR, format(result.lootedElixir), result.lootedElixir === 0],
    [DARK_ELIXIR, format(result.lootedDarkElixir), result.lootedDarkElixir === 0],
    [TROPHY, trophy, false]
  ];

  el.ovLoot.innerHTML = spoils
    .map(([icon, value, zero]) => `<li${zero ? ' class="zero"' : ''}>${icon}${value}</li>`)
    .join('');

  const looted = result.lootedGold + result.lootedElixir + result.lootedDarkElixir;
  el.overlayTitle.title = looted === 0 ? 'That village had nothing left to steal.' : '';

  el.overlay.removeAttribute('hidden');
}

async function scoutPlayer(playerId) {
  if (state.busy) return;
  try {
    if (playerId === state.player.id) {
      if (state.homeVillage) await showVillage(state.homeVillage.id, { home: true });
      return;
    }
    const known = state.targets.filter((v) => v.playerId === playerId);
    const villages = known.length ? known : await api.villages(playerId);
    if (!villages.length) return toast('That chief has no village yet.');
    await showVillage(villages[0].id);
  } catch (error) {
    toast(describe(error), 'err');
  }
}

el.playerSelect.addEventListener('change', (event) => {
  selectPlayer(Number(event.target.value)).catch((error) => toast(describe(error), 'err'));
});
el.villageSelect.addEventListener('change', (event) => {
  showVillage(Number(event.target.value), { home: true }).catch((error) => toast(describe(error), 'err'));
});
el.homeBtn.addEventListener('click', () => {
  if (!state.homeVillage) return;
  showVillage(state.homeVillage.id, { home: true }).catch((error) => toast(describe(error), 'err'));
});
el.buildBtn.addEventListener('click', build);
el.collectBtn.addEventListener('click', collect);
el.targetSelect.addEventListener('change', () => { renderTargetLoot(); updateCamp(); });
el.buildType.addEventListener('change', updateBuildAffordability);
el.attackBtn.addEventListener('click', attack);
el.overlayClose.addEventListener('click', () => {
  el.overlay.setAttribute('hidden', '');
  if (!state.homeVillage) return;
  showVillage(state.homeVillage.id, { home: true }).catch((error) => toast(describe(error), 'err'));
});

for (const tab of document.querySelectorAll('.tab')) {
  tab.addEventListener('click', () => {
    for (const other of document.querySelectorAll('.tab')) other.classList.toggle('active', other === tab);
    $('tab-army').hidden = tab.dataset.tab !== 'army';
    $('tab-raid').hidden = tab.dataset.tab !== 'raid';
  });
}

boot();
