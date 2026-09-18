const BASE = '/api';

class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

async function request(path, options = {}) {
  const response = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });

  const text = await response.text();

  let body = null;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    if (response.ok) throw new ApiError(response.status, 'The server sent something that is not JSON.');
  }

  if (!response.ok) {
    throw new ApiError(response.status, (body && (body.message || body.error)) || response.statusText);
  }
  return body;
}

export const api = {
  players: () => request('/players'),
  player: (id) => request(`/players/${id}`),
  rank: (id) => request(`/players/${id}/rank`),
  leaderboard: (limit) => request(limit ? `/leaderboard?limit=${limit}` : '/leaderboard'),
  troops: (playerId) => request(`/players/${playerId}/troops`),
  villages: (playerId) => request(`/players/${playerId}/villages`),
  allVillages: () => request('/villages'),
  village: (id) => request(`/villages/${id}`),
  troopTypes: () => request('/troop-types'),
  buildingTypes: () => request('/building-types'),
  raid: (payload) => request('/raids', { method: 'POST', body: JSON.stringify(payload) }),
  collect: (villageId) => request(`/villages/${villageId}/collect`, { method: 'POST' }),
  upgradeBuilding: (id) => request(`/buildings/${id}/upgrade`, { method: 'POST' }),
  upgradeTroop: (id) => request(`/troops/${id}/upgrade`, { method: 'POST' }),
  addBuilding: (villageId, payload) =>
    request(`/villages/${villageId}/buildings`, { method: 'POST', body: JSON.stringify(payload) })
};

export { ApiError };
