import * as THREE from 'three';

export const PALETTE = {
  grass: 0x76a939,
  grassDark: 0x5d8a2c,
  dirt: 0x9c7a4a,
  stone: 0x9aa0a6,
  stoneDark: 0x6f767c,
  wood: 0x8a5a2b,
  woodDark: 0x5e3c1c,
  roof: 0xc0392b,
  roofBlue: 0x3f72af,
  gold: 0xffc233,
  elixir: 0xd84fe0,
  darkElixir: 0x5b2fb0,
  tesla: 0x5ad7ff,
  monolith: 0x2e1a47,
  canvasTent: 0xe8d5a8
};

const mat = (color, opts = {}) => new THREE.MeshLambertMaterial({ color, ...opts });

function box(w, h, d, color, y = 0) {
  const mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), mat(color));
  mesh.position.y = y + h / 2;
  mesh.castShadow = true;
  mesh.receiveShadow = true;
  return mesh;
}

function cylinder(rTop, rBottom, h, color, y = 0, segments = 12) {
  const mesh = new THREE.Mesh(new THREE.CylinderGeometry(rTop, rBottom, h, segments), mat(color));
  mesh.position.y = y + h / 2;
  mesh.castShadow = true;
  mesh.receiveShadow = true;
  return mesh;
}

function cone(r, h, color, y = 0, segments = 12) {
  const mesh = new THREE.Mesh(new THREE.ConeGeometry(r, h, segments), mat(color));
  mesh.position.y = y + h / 2;
  mesh.castShadow = true;
  return mesh;
}

function sphere(r, color, y = 0) {
  const mesh = new THREE.Mesh(new THREE.SphereGeometry(r, 14, 10), mat(color));
  mesh.position.y = y + r;
  mesh.castShadow = true;
  return mesh;
}

function plinth(size, color = PALETTE.stoneDark) {
  const mesh = box(size, 0.22, size, color, 0);
  mesh.position.y = 0.11;
  return mesh;
}

const BUILDERS = {
  CANNON(group) {
    group.add(plinth(1.9));
    group.add(cylinder(0.62, 0.78, 0.5, PALETTE.stone, 0.2));
    const barrel = cylinder(0.2, 0.26, 1.25, PALETTE.stoneDark, 0);
    barrel.rotation.z = Math.PI / 2.6;
    barrel.position.set(0.3, 0.95, 0);
    group.add(barrel);
    group.add(sphere(0.18, PALETTE.woodDark, 0.62));
  },

  RICOCHET_CANNON(group) {
    group.add(plinth(2.3, PALETTE.stone));
    group.add(cylinder(0.75, 0.95, 0.65, PALETTE.stoneDark, 0.2));
    for (const side of [-1, 1]) {
      const barrel = cylinder(0.17, 0.22, 1.35, PALETTE.gold, 0);
      barrel.rotation.z = Math.PI / 2.6;
      barrel.position.set(0.32, 1.05, side * 0.3);
      group.add(barrel);
    }
  },

  ARCHER_TOWER(group) {
    group.add(plinth(1.8));
    group.add(cylinder(0.42, 0.55, 1.5, PALETTE.wood, 0.2));
    group.add(cylinder(0.6, 0.6, 0.22, PALETTE.woodDark, 1.7));
    group.add(cone(0.68, 0.75, PALETTE.roof, 1.92, 8));
  },

  MULTI_ARCHER_TOWER(group) {
    group.add(plinth(2.2));
    for (const [x, z, h] of [[-0.35, -0.2, 1.5], [0.35, 0.25, 1.9]]) {
      const shaft = cylinder(0.32, 0.42, h, PALETTE.wood, 0.2);
      shaft.position.x = x;
      shaft.position.z = z;
      group.add(shaft);
      const roof = cone(0.5, 0.62, PALETTE.roofBlue, 0.2 + h, 8);
      roof.position.x = x;
      roof.position.z = z;
      group.add(roof);
    }
  },

  MORTAR(group) {
    group.add(plinth(2.1, PALETTE.dirt));
    group.add(cylinder(0.85, 0.7, 0.55, PALETTE.stoneDark, 0.2));
    const bowl = cylinder(0.78, 0.5, 0.8, PALETTE.stone, 0.7);
    bowl.rotation.x = -0.18;
    group.add(bowl);
    group.add(sphere(0.3, PALETTE.monolith, 1.25));
  },

  MULTI_GEAR_TOWER(group) {
    group.add(plinth(2.1, PALETTE.dirt));
    group.add(box(1.1, 0.9, 1.1, PALETTE.stoneDark, 0.2));
    for (const side of [-1, 1]) {
      const gear = cylinder(0.42, 0.42, 0.16, PALETTE.gold, 0);
      gear.rotation.x = Math.PI / 2;
      gear.position.set(side * 0.55, 0.85, 0);
      group.add(gear);
    }
    group.add(cylinder(0.3, 0.3, 0.7, PALETTE.stone, 1.1));
  },

  WIZARD_TOWER(group) {
    group.add(plinth(1.8));
    group.add(cylinder(0.4, 0.55, 1.35, PALETTE.stone, 0.2));
    group.add(cylinder(0.62, 0.62, 0.2, PALETTE.woodDark, 1.55));
    group.add(cone(0.6, 1.0, 0x6a4fc0, 1.75, 8));
    const orb = sphere(0.2, PALETTE.elixir, 2.6);
    orb.material.emissive = new THREE.Color(PALETTE.elixir);
    orb.material.emissiveIntensity = 0.6;
    group.add(orb);
  },

  AIR_DEFENSE(group) {
    group.add(plinth(2.0, PALETTE.stone));
    group.add(cylinder(0.5, 0.65, 0.6, PALETTE.stoneDark, 0.2));
    const rack = new THREE.Group();
    for (const dx of [-0.22, 0, 0.22]) {
      const tube = cylinder(0.12, 0.12, 1.5, 0x4d6b8a, 0);
      tube.position.set(dx, 0.75, 0);
      rack.add(tube);
    }
    rack.rotation.z = -0.5;
    rack.position.y = 0.6;
    group.add(rack);
  },

  HIDDEN_TESLA(group) {
    group.add(plinth(1.6, PALETTE.dirt));
    group.add(box(0.85, 0.5, 0.85, PALETTE.stoneDark, 0.2));
    group.add(cylinder(0.22, 0.28, 0.9, PALETTE.stone, 0.7));
    const coil = sphere(0.26, PALETTE.tesla, 1.6);
    coil.material.emissive = new THREE.Color(PALETTE.tesla);
    coil.material.emissiveIntensity = 0.8;
    group.add(coil);
  },

  MONOLITH(group) {
    group.add(plinth(2.2, PALETTE.monolith));
    const shaft = box(0.9, 2.6, 0.9, PALETTE.monolith, 0.2);
    shaft.rotation.y = 0.35;
    group.add(shaft);
    const crystal = new THREE.Mesh(new THREE.OctahedronGeometry(0.42), mat(PALETTE.darkElixir));
    crystal.material.emissive = new THREE.Color(PALETTE.darkElixir);
    crystal.material.emissiveIntensity = 0.7;
    crystal.position.y = 3.2;
    group.add(crystal);
    group.userData.spin = crystal;
  },

  GOLD_MINE(group) {
    group.add(plinth(2.0, PALETTE.dirt));
    group.add(cylinder(0.75, 0.9, 0.45, PALETTE.dirt, 0.2));
    const drill = cone(0.3, 1.1, PALETTE.gold, 0.6);
    group.add(drill);
    group.add(box(1.5, 0.18, 0.3, PALETTE.wood, 0.55));
  },

  ELIXIR_COLLECTOR(group) {
    group.add(plinth(2.0, PALETTE.dirt));
    group.add(cylinder(0.75, 0.9, 0.45, PALETTE.woodDark, 0.2));
    group.add(cone(0.3, 1.1, PALETTE.elixir, 0.6));
    group.add(box(1.5, 0.18, 0.3, PALETTE.wood, 0.55));
  },

  DARK_ELIXIR_DRILL(group) {
    group.add(plinth(2.0, PALETTE.monolith));
    group.add(cylinder(0.72, 0.9, 0.5, 0x3b2a55, 0.2));
    const shaft = cylinder(0.16, 0.16, 1.3, 0x6f767c, 0.6);
    group.add(shaft);
    const drop = sphere(0.26, PALETTE.darkElixir, 1.75);
    drop.material.emissive = new THREE.Color(PALETTE.darkElixir);
    drop.material.emissiveIntensity = 0.55;
    group.add(drop);
    group.userData.spin = shaft;
    for (const side of [-1, 1]) {
      const leg = box(0.16, 0.9, 0.16, PALETTE.woodDark, 0.2);
      leg.position.set(side * 0.62, leg.position.y, 0);
      group.add(leg);
    }
  },

  DARK_ELIXIR_STORAGE(group) {
    group.add(plinth(2.2, PALETTE.monolith));
    group.add(cylinder(0.7, 0.8, 1.5, 0x3b2a55, 0.2));
    for (const y of [0.5, 1.1]) {
      const band = cylinder(0.76, 0.76, 0.12, 0x6f767c, y);
      group.add(band);
    }
    const fluid = cylinder(0.62, 0.62, 0.3, PALETTE.darkElixir, 1.55);
    fluid.material.emissive = new THREE.Color(PALETTE.darkElixir);
    fluid.material.emissiveIntensity = 0.5;
    group.add(fluid);
  },

  GOLD_STORAGE(group) {
    group.add(plinth(2.1, PALETTE.stoneDark));
    group.add(box(1.25, 1.1, 1.25, PALETTE.wood, 0.2));
    group.add(box(1.4, 0.2, 1.4, PALETTE.woodDark, 1.3));
    const pile = cone(0.5, 0.55, PALETTE.gold, 1.5, 8);
    group.add(pile);
  },

  ELIXIR_STORAGE(group) {
    group.add(plinth(2.1, PALETTE.stoneDark));
    group.add(cylinder(0.65, 0.72, 1.35, PALETTE.stone, 0.2));
    const fluid = cylinder(0.58, 0.58, 0.3, PALETTE.elixir, 1.35);
    fluid.material.emissive = new THREE.Color(PALETTE.elixir);
    fluid.material.emissiveIntensity = 0.35;
    group.add(fluid);
  },

  LABORATORY(group) {
    group.add(plinth(2.3));
    group.add(cylinder(0.85, 1.0, 0.8, PALETTE.stone, 0.2));
    const dome = new THREE.Mesh(new THREE.SphereGeometry(0.85, 16, 10, 0, Math.PI * 2, 0, Math.PI / 2), mat(0x7b5ea7));
    dome.position.y = 1.0;
    dome.castShadow = true;
    group.add(dome);
    const flask = sphere(0.22, PALETTE.elixir, 1.85);
    flask.material.emissive = new THREE.Color(PALETTE.elixir);
    flask.material.emissiveIntensity = 0.5;
    group.add(flask);
  },

  HEROHALL(group) {
    group.add(plinth(2.8, PALETTE.stone));
    group.add(box(1.8, 1.2, 1.5, PALETTE.wood, 0.2));
    const roof = cone(1.45, 0.9, PALETTE.roof, 1.4, 4);
    roof.rotation.y = Math.PI / 4;
    group.add(roof);
    for (const side of [-1, 1]) {
      const pillar = cylinder(0.16, 0.18, 1.3, PALETTE.stone, 0.2);
      pillar.position.set(side * 0.78, pillar.position.y, 0.8);
      group.add(pillar);
    }
    const banner = box(0.05, 0.5, 0.35, PALETTE.darkElixir, 1.5);
    banner.position.z = 0.8;
    group.add(banner);
  },

  MILITARY_CAMP(group) {
    group.add(plinth(2.4, PALETTE.dirt));
    for (const [x, z] of [[-0.45, -0.35], [0.45, -0.2], [0, 0.5]]) {
      const tent = cone(0.42, 0.7, PALETTE.canvasTent, 0.2, 6);
      tent.position.x = x;
      tent.position.z = z;
      group.add(tent);
    }
    const fire = sphere(0.14, 0xff8a3d, 0.25);
    fire.material.emissive = new THREE.Color(0xff6a1d);
    fire.material.emissiveIntensity = 0.9;
    group.add(fire);
  },

  SPELL_FACTORY(group) {
    group.add(plinth(2.1));
    group.add(box(1.2, 0.9, 1.2, PALETTE.stone, 0.2));
    group.add(cone(0.95, 0.7, 0x4a7ab5, 1.1, 6));
    const cauldron = cylinder(0.34, 0.28, 0.4, PALETTE.stoneDark, 1.8);
    group.add(cauldron);
    const brew = cylinder(0.3, 0.3, 0.1, PALETTE.elixir, 2.15);
    brew.material.emissive = new THREE.Color(PALETTE.elixir);
    brew.material.emissiveIntensity = 0.6;
    group.add(brew);
  },

  PET_HOUSE(group) {
    group.add(plinth(2.1, PALETTE.dirt));
    group.add(box(1.3, 0.8, 1.1, PALETTE.woodDark, 0.2));
    const roof = box(1.5, 0.22, 1.3, PALETTE.roof, 1.0);
    roof.rotation.x = 0.06;
    group.add(roof);
    const door = cylinder(0.26, 0.26, 0.08, 0x2b1a0c, 0);
    door.rotation.x = Math.PI / 2;
    door.position.set(0, 0.55, 0.57);
    group.add(door);
  }
};

function defaultBuilding(group) {
  group.add(plinth(2.0));
  group.add(box(1.1, 0.9, 1.1, PALETTE.stone, 0.2));
  group.add(cone(0.85, 0.6, PALETTE.roof, 1.1, 6));
}

export function createBuilding(building) {
  const group = new THREE.Group();
  (BUILDERS[building.type] || defaultBuilding)(group);

  const scale = 1 + Math.min(building.level, 12) * 0.018;
  group.scale.setScalar(scale);
  group.userData.building = building;
  return group;
}

const TROOP_COLORS = {
  BARBARIAN: 0xf2c07a,
  ARCHER: 0x8ad6a0,
  GIANT: 0xd9a066,
  GOBLIN: 0x7bc043,
  BALLOON: 0xe05c5c,
  WIZARD: 0x6a4fc0,
  HOG_RIDER: 0x9a6b3f,
  HEALER: 0xf7d6e0,
  DRAGON: 0xc0392b
};

export function createTroop(type, movement) {
  const group = new THREE.Group();
  const color = TROOP_COLORS[type] || 0xdddddd;
  const radius = type === 'GIANT' ? 0.3 : 0.2;

  const walker = (r, bodyColor) => {
    group.add(cylinder(r * 0.8, r, r * 2.4, bodyColor, 0));
    group.add(sphere(r * 0.9, bodyColor, r * 2.4));
    return r * 2.4 + r * 1.8;
  };

  if (type === 'BALLOON') {
    group.add(sphere(0.3, color, 0.25));
    group.add(box(0.26, 0.2, 0.26, PALETTE.wood, 0.1));

  } else if (type === 'DRAGON') {
    const body = cylinder(0.2, 0.3, 0.8, color, 0.1);
    body.rotation.z = Math.PI / 2;
    group.add(body);
    for (const side of [-1, 1]) {
      const wing = box(0.5, 0.06, 0.34, 0xe07a5a, 0.35);
      wing.position.z = side * 0.32;
      wing.rotation.x = side * 0.35;
      group.add(wing);
      group.userData.wings = group.userData.wings || [];
      group.userData.wings.push(wing);
    }
    group.add(sphere(0.18, color, 0.5));

  } else if (type === 'HEALER') {
    group.add(sphere(0.24, color, 0.2));
    const halo = new THREE.Mesh(new THREE.TorusGeometry(0.26, 0.05, 8, 16), mat(0xfff0a0));
    halo.rotation.x = Math.PI / 2;
    halo.position.y = 0.78;
    group.add(halo);

  } else if (type === 'BARBARIAN') {
    const head = walker(radius, color);
    group.add(cone(radius * 0.95, 0.16, 0xc9922f, head - 0.1, 8));
    const sword = box(0.05, 0.5, 0.11, 0xc9ccd1, 0.3);
    sword.position.set(0.26, sword.position.y, 0);
    sword.rotation.z = -0.3;
    group.add(sword);

  } else if (type === 'GIANT') {
    const head = walker(radius, color);
    group.add(sphere(radius * 0.45, color, head - 0.16));
    for (const side of [-1, 1]) {
      const arm = cylinder(0.09, 0.11, 0.55, color, 0.25);
      arm.position.set(side * (radius + 0.11), arm.position.y, 0);
      group.add(arm);
    }

  } else if (type === 'GOBLIN') {
    const head = walker(radius * 0.8, color);
    for (const side of [-1, 1]) {
      const ear = cone(0.07, 0.24, color, head - 0.28, 5);
      ear.position.set(side * 0.17, ear.position.y, 0);
      ear.rotation.z = side * 0.9;
      group.add(ear);
    }
    const sack = sphere(0.14, PALETTE.gold, 0.08);
    sack.position.z = -0.22;
    group.add(sack);

  } else if (type === 'HOG_RIDER') {
    const hog = cylinder(0.17, 0.2, 0.62, 0x6f5540, 0.08);
    hog.rotation.z = Math.PI / 2;
    group.add(hog);
    for (const [x, z] of [[-0.2, -0.13], [0.2, -0.13], [-0.2, 0.13], [0.2, 0.13]]) {
      const leg = cylinder(0.05, 0.05, 0.22, 0x53412f, 0);
      leg.position.set(x, leg.position.y, z);
      group.add(leg);
    }
    group.add(sphere(0.16, color, 0.42));
    const hammer = box(0.18, 0.13, 0.13, 0x8a5a2b, 0.72);
    hammer.position.x = 0.22;
    group.add(hammer);

  } else {
    const head = walker(radius, color);
    if (type === 'WIZARD') group.add(cone(radius, 0.35, 0x4a2f8a, head, 8));
    if (type === 'ARCHER') group.add(cone(radius * 0.8, 0.22, 0x4f8a5a, head, 6));
  }

  group.userData.airborne = movement === 'AIR';
  group.userData.troopType = type;
  return group;
}

function rng(seed) {
  let state = seed >>> 0;
  return () => {
    state = (state * 1664525 + 1013904223) >>> 0;
    return state / 4294967296;
  };
}

export function createIsland(size) {
  const group = new THREE.Group();
  const half = size / 2;

  const water = new THREE.Mesh(
    new THREE.CircleGeometry(size * 2.6, 48),
    mat(0x2f8fd6, { transparent: true, opacity: 0.92 })
  );
  water.rotation.x = -Math.PI / 2;
  water.position.y = -1.55;
  group.add(water);

  const foam = new THREE.Mesh(new THREE.CircleGeometry(half * 1.34, 40), mat(0x8fd6f2));
  foam.rotation.x = -Math.PI / 2;
  foam.position.y = -1.4;
  group.add(foam);

  const beach = new THREE.Mesh(new THREE.CylinderGeometry(half * 1.26, half * 1.16, 0.7, 40), mat(0xe8d9a8));
  beach.position.y = -1.05;
  beach.receiveShadow = true;
  group.add(beach);

  const cliff = new THREE.Mesh(new THREE.BoxGeometry(size + 1.2, 1.5, size + 1.2), mat(0x8a6234));
  cliff.position.y = -0.9;
  group.add(cliff);

  const lawn = new THREE.Mesh(new THREE.BoxGeometry(size, 0.5, size), mat(PALETTE.grass));
  lawn.position.y = -0.25;
  lawn.receiveShadow = true;
  group.add(lawn);

  const tiles = new THREE.Group();
  const step = size / 12;
  for (let i = 0; i < 12; i++) {
    for (let j = 0; j < 12; j++) {
      if ((i + j) % 2 !== 0) continue;
      const tile = new THREE.Mesh(
        new THREE.PlaneGeometry(step, step),
        mat(PALETTE.grassDark, { transparent: true, opacity: 0.4 })
      );
      tile.rotation.x = -Math.PI / 2;
      tile.position.set(-half + step * (i + 0.5), 0.012, -half + step * (j + 0.5));
      tiles.add(tile);
    }
  }
  group.add(tiles);

  return group;
}

function tree(scale) {
  const group = new THREE.Group();
  group.add(cylinder(0.12, 0.18, 0.7, PALETTE.woodDark, 0));
  const crown = sphere(0.52, 0x4e9134, 0.5);
  group.add(crown);
  const top = sphere(0.36, 0x63ad42, 1.0);
  group.add(top);
  group.scale.setScalar(scale);
  return group;
}

function rock(scale) {
  const mesh = new THREE.Mesh(new THREE.DodecahedronGeometry(0.42), mat(0x8d9299, { flatShading: true }));
  mesh.position.y = 0.2;
  mesh.rotation.set(Math.random(), Math.random(), Math.random());
  mesh.castShadow = true;
  mesh.scale.setScalar(scale);
  return mesh;
}

function bush() {
  const group = new THREE.Group();
  group.add(sphere(0.26, 0x4e9134, 0));
  const side = sphere(0.18, 0x63ad42, 0.02);
  side.position.x = 0.24;
  group.add(side);
  return group;
}

export function createScenery(size, keepOutRadius) {
  const group = new THREE.Group();
  const random = rng(20260918);
  const half = size / 2;

  for (let i = 0; i < 90; i++) {
    const x = (random() * 2 - 1) * (half - 0.8);
    const z = (random() * 2 - 1) * (half - 0.8);
    if (Math.hypot(x, z) < keepOutRadius) continue;

    const roll = random();
    const prop = roll < 0.5 ? tree(0.8 + random() * 0.5)
      : roll < 0.8 ? rock(0.6 + random() * 0.6)
      : bush();

    prop.position.set(x, 0, z);
    prop.rotation.y = random() * Math.PI * 2;
    group.add(prop);
  }

  return group;
}

export function createWallRing(radius, segments) {
  const group = new THREE.Group();

  for (let i = 0; i < segments; i++) {
    if (i % 7 === 0) continue;

    const angle = (i / segments) * Math.PI * 2;
    const block = new THREE.Group();

    const body = box(1.15, 0.85, 0.8, 0xb9b3a6, 0);
    block.add(body);
    for (const dx of [-0.36, 0, 0.36]) {
      const merlon = box(0.26, 0.24, 0.8, 0xd2cabb, 0.85);
      merlon.position.x = dx;
      block.add(merlon);
    }

    block.position.set(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
    block.rotation.y = -angle;
    group.add(block);
  }

  return group;
}

export function createClouds(size) {
  const group = new THREE.Group();
  const random = rng(77);

  for (let i = 0; i < 9; i++) {
    const cloud = new THREE.Group();
    const material = mat(0xffffff, { transparent: true, opacity: 0.88 });

    for (let j = 0; j < 4; j++) {
      const blob = new THREE.Mesh(new THREE.SphereGeometry(0.9 + random() * 0.8, 10, 7), material);
      blob.position.set((random() - 0.5) * 3.4, (random() - 0.5) * 0.5, (random() - 0.5) * 1.8);
      cloud.add(blob);
    }

    const angle = random() * Math.PI * 2;
    const distance = size * (1.5 + random() * 0.9);
    cloud.position.set(Math.cos(angle) * distance, 16 + random() * 9, Math.sin(angle) * distance);
    cloud.userData.drift = 0.12 + random() * 0.2;
    group.add(cloud);
  }

  return group;
}
