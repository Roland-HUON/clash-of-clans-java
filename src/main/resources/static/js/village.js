import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { createBuilding, createClouds, createIsland, createScenery, createTroop, createWallRing, PALETTE } from './meshes.js';

const GROUND_SIZE = 32;
const RINGS = { core: 0, middle: 7.4, outer: 11.2 };

function familyOf(type) {
  if (type.defensive) return 'def';
  if (type.resource) return 'res';
  return 'core';
}

function ringOf(family) {
  if (family === 'def') return 'outer';
  if (family === 'res') return 'middle';
  return 'core';
}

function release(object) {
  object.traverse((node) => {
    if (node.geometry) node.geometry.dispose();
    if (!node.material) return;
    for (const material of Array.isArray(node.material) ? node.material : [node.material]) {
      material.dispose();
    }
  });
}

function healthBar() {
  const group = new THREE.Group();

  const back = new THREE.Mesh(
    new THREE.PlaneGeometry(1.5, 0.22),
    new THREE.MeshBasicMaterial({ color: 0x1b1008, depthTest: false, transparent: true })
  );
  const fill = new THREE.Mesh(
    new THREE.PlaneGeometry(1.4, 0.14),
    new THREE.MeshBasicMaterial({ color: 0x4ddb5a, depthTest: false, transparent: true })
  );
  fill.position.z = 0.01;

  group.add(back, fill);
  group.renderOrder = 999;
  group.userData.fill = fill;
  group.visible = false;
  return group;
}

export class VillageScene {
  constructor(host) {
    this.host = host;
    this.buildings = new Map();
    this.troops = [];
    this.effects = [];
    this.clock = new THREE.Clock();

    this.scene = new THREE.Scene();
    this.scene.background = new THREE.Color(0x7ec8f2);
    this.scene.fog = new THREE.Fog(0x9ad6f5, 60, 130);

    this.camera = new THREE.PerspectiveCamera(46, 1, 0.1, 200);
    this.camera.position.set(21, 20, 21);

    this.renderer = new THREE.WebGLRenderer({ antialias: true });
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.renderer.shadowMap.enabled = true;
    this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    host.appendChild(this.renderer.domElement);

    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enableDamping = true;
    this.controls.dampingFactor = 0.08;
    this.controls.minDistance = 14;
    this.controls.maxDistance = 70;
    this.controls.maxPolarAngle = Math.PI / 2.25;
    this.controls.target.set(0, 1.4, 0);

    this.scene.add(new THREE.HemisphereLight(0xdff0ff, 0x6c8f3a, 1.15));

    const sun = new THREE.DirectionalLight(0xfff0c8, 1.7);
    sun.position.set(14, 22, 10);
    sun.castShadow = true;
    sun.shadow.mapSize.set(2048, 2048);
    sun.shadow.camera.left = -22;
    sun.shadow.camera.right = 22;
    sun.shadow.camera.top = 22;
    sun.shadow.camera.bottom = -22;
    this.scene.add(sun);

    this.world = new THREE.Group();
    this.scene.add(this.world);
    this.world.add(createIsland(GROUND_SIZE));
    this.world.add(createScenery(GROUND_SIZE, RINGS.outer + 2.6));
    this.world.add(createWallRing(RINGS.outer + 1.6, 36));

    this.clouds = createClouds(GROUND_SIZE);
    this.scene.add(this.clouds);

    this.raycaster = new THREE.Raycaster();
    this.pointer = new THREE.Vector2();
    this.onHover = null;

    this.renderer.domElement.addEventListener('pointermove', (event) => this._hover(event));
    this.renderer.domElement.addEventListener('pointerleave', () => this.onHover && this.onHover(null));

    this._resize();
    new ResizeObserver(() => this._resize()).observe(host);
    this.renderer.setAnimationLoop(() => this._frame());
  }

  _resize() {
    const { clientWidth: w, clientHeight: h } = this.host;
    if (!w || !h) return;
    this.camera.aspect = w / h;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(w, h);
  }

  _hover(event) {
    if (!this.onHover) return;
    const rect = this.renderer.domElement.getBoundingClientRect();
    this.pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
    this.pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
    this.raycaster.setFromCamera(this.pointer, this.camera);

    const roots = [...this.buildings.values()].map((entry) => entry.group);
    const hit = this.raycaster.intersectObjects(roots, true)[0];

    if (!hit) return this.onHover(null);

    let node = hit.object;
    while (node && !node.userData.building) node = node.parent;
    this.onHover(node ? { building: node.userData.building, x: event.clientX, y: event.clientY } : null);
  }

  setVillage(village, typesById) {
    for (const { group, bar } of this.buildings.values()) {
      this.world.remove(group);
      this.world.remove(bar);
      release(group);
      release(bar);
    }
    this.buildings.clear();
    this.clearArmy();
    this.clearEffects();

    const byRing = { core: [], middle: [], outer: [] };
    for (const building of village.buildings) {
      const type = typesById.get(building.type) || {};
      const family = familyOf(type);
      byRing[ringOf(family)].push({ building, family });
    }

    for (const [ring, entries] of Object.entries(byRing)) {
      const radius = RINGS[ring];
      const rows = Math.max(1, Math.ceil(entries.length / 16));
      const perRow = Math.ceil(entries.length / rows);

      entries.forEach((entry, index) => {
        const group = createBuilding(entry.building);
        const row = index % rows;
        const slot = Math.floor(index / rows);
        const angle = (slot / Math.max(perRow, 1)) * Math.PI * 2 + (ring === 'outer' ? 0.24 : 0);

        if (radius === 0) {
          const spread = entries.length > 1 ? 2.6 + row * 2.2 : 0;
          group.position.set(Math.cos(angle) * spread, 0, Math.sin(angle) * spread);
        } else {
          const ringRadius = radius - row * 2.4;
          group.position.set(Math.cos(angle) * ringRadius, 0, Math.sin(angle) * ringRadius);
        }
        group.rotation.y = -angle + Math.PI / 2;
        group.userData.family = entry.family;

        const bar = healthBar();
        bar.position.copy(group.position);
        bar.position.y = 3.4;

        this.world.add(group, bar);
        this.buildings.set(entry.building.id, { group, bar, alive: true, family: entry.family });
      });
    }
  }

  highlight(buildingId) {
    for (const [id, entry] of this.buildings) {
      const lifted = id === buildingId ? 0.45 : 0;
      entry.group.userData.lift = lifted;
    }
  }

  setHealth(buildingId, ratio) {
    const entry = this.buildings.get(buildingId);
    if (!entry) return;
    entry.bar.visible = ratio < 1;
    entry.bar.userData.fill.scale.x = Math.max(ratio, 0.001);
    entry.bar.userData.fill.position.x = -(1 - Math.max(ratio, 0)) * 0.7;
    entry.bar.userData.fill.material.color.setHex(ratio > 0.5 ? 0x4ddb5a : ratio > 0.2 ? 0xffc233 : 0xe04a3f);
  }

  destroyBuilding(buildingId) {
    const entry = this.buildings.get(buildingId);
    if (!entry || !entry.alive) return;
    entry.alive = false;
    entry.bar.visible = false;
    this.effects.push({ kind: 'collapse', group: entry.group, t: 0 });
    this._puff(entry.group.position);
  }

  _puff(position) {
    for (let i = 0; i < 7; i++) {
      const puff = new THREE.Mesh(
        new THREE.SphereGeometry(0.28 + Math.random() * 0.2, 8, 6),
        new THREE.MeshLambertMaterial({ color: 0xd8d2c4, transparent: true, opacity: 0.85 })
      );
      puff.position.copy(position);
      puff.position.y += 0.5;
      this.world.add(puff);
      this.effects.push({
        kind: 'puff',
        group: puff,
        t: 0,
        drift: new THREE.Vector3((Math.random() - 0.5) * 2.2, 1.4 + Math.random(), (Math.random() - 0.5) * 2.2)
      });
    }
  }

  spawnArmy(units) {
    this.clearArmy();
    let index = 0;
    const total = units.length || 1;

    for (const unit of units) {
      const mesh = createTroop(unit.type, unit.movement);
      const angle = (index / total) * Math.PI * 2;
      const radius = GROUND_SIZE / 2 + 1.5;

      mesh.position.set(Math.cos(angle) * radius, mesh.userData.airborne ? 2.2 : 0, Math.sin(angle) * radius);
      mesh.userData.home = mesh.position.clone();
      mesh.userData.phase = Math.random() * Math.PI * 2;
      mesh.userData.alive = true;

      this.world.add(mesh);
      this.troops.push(mesh);
      index++;
    }
  }

  marchTo(target = new THREE.Vector3(0, 0, 0)) {
    this.troops.forEach((troop, index) => {
      if (!troop.userData.alive) return;
      const angle = (index / Math.max(this.troops.length, 1)) * Math.PI * 2;
      const radius = 3.4 + (index % 3) * 1.1;
      troop.userData.goal = new THREE.Vector3(
        target.x + Math.cos(angle) * radius,
        troop.userData.airborne ? 2.2 : 0,
        target.z + Math.sin(angle) * radius
      );
    });
  }

  killTroop(count) {
    let killed = 0;
    for (const troop of this.troops) {
      if (killed >= count) break;
      if (!troop.userData.alive) continue;
      troop.userData.alive = false;
      this.effects.push({ kind: 'fade', group: troop, t: 0 });
      killed++;
    }
  }

  clearArmy() {
    for (const troop of this.troops) {
      this.world.remove(troop);
      release(troop);
    }
    this.troops = [];
  }

  clearEffects() {
    for (const effect of this.effects) {
      this.world.remove(effect.group);
      if (effect.kind === 'puff') release(effect.group);
    }
    this.effects = [];
  }

  resetCamera() {
    this.controls.target.set(0, 1.4, 0);
    this.camera.position.set(21, 20, 21);
  }

  _frame() {
    const delta = Math.min(this.clock.getDelta(), 0.05);
    const time = this.clock.elapsedTime;

    for (const entry of this.buildings.values()) {
      const target = entry.alive ? (entry.group.userData.lift || 0) : entry.group.position.y;
      if (entry.alive) entry.group.position.y += (target - entry.group.position.y) * 0.15;
      entry.bar.quaternion.copy(this.camera.quaternion);

      const spin = entry.group.userData.spin;
      if (spin && entry.alive) spin.rotation.y += delta * 1.6;
    }

    for (const troop of this.troops) {
      if (!troop.userData.alive) continue;
      troop.lookAt(0, troop.position.y, 0);

      if (troop.userData.goal) {
        troop.position.lerp(troop.userData.goal, 0.018);
      }
      if (troop.userData.airborne) {
        troop.position.y = 2.2 + Math.sin(time * 2.2 + troop.userData.phase) * 0.22;
        for (const wing of troop.userData.wings || []) {
          wing.rotation.x = Math.sin(time * 9 + troop.userData.phase) * 0.5;
        }
      } else {
        troop.position.y = Math.abs(Math.sin(time * 6 + troop.userData.phase)) * 0.09;
      }
    }

    this.effects = this.effects.filter((effect) => {
      effect.t += delta;

      if (effect.kind === 'collapse') {
        effect.group.position.y = -effect.t * 1.6;
        effect.group.rotation.z = effect.t * 0.5;
        effect.group.scale.multiplyScalar(1 - delta * 0.7);
        if (effect.t > 1.6) {
          effect.group.visible = false;
          this.world.remove(effect.group);
          return false;
        }
        return true;
      }

      if (effect.kind === 'puff') {
        effect.group.position.addScaledVector(effect.drift, delta);
        effect.group.material.opacity = Math.max(0, 0.85 - effect.t);
        effect.group.scale.multiplyScalar(1 + delta * 0.8);
        if (effect.t > 0.9) {
          this.world.remove(effect.group);
          release(effect.group);
          return false;
        }
        return true;
      }

      if (effect.kind === 'fade') {
        effect.group.position.y -= delta * 0.8;
        effect.group.scale.multiplyScalar(1 - delta * 1.6);
        if (effect.t > 0.7) {
          effect.group.visible = false;
          this.world.remove(effect.group);
          return false;
        }
        return true;
      }

      return false;
    });

    for (const cloud of this.clouds.children) {
      cloud.position.x += cloud.userData.drift * delta;
      if (cloud.position.x > GROUND_SIZE * 2.4) cloud.position.x = -GROUND_SIZE * 2.4;
    }

    this.controls.update();
    this.renderer.render(this.scene, this.camera);
  }
}

export { GROUND_SIZE, PALETTE };
