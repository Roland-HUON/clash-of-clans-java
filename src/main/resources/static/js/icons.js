const svg = (viewBox, body) =>
  `<svg class="ico" viewBox="${viewBox}" aria-hidden="true">${body}</svg>`;

export const GOLD = svg('0 0 24 24', `
  <circle cx="12" cy="12" r="10" fill="#e39a12" stroke="#5a3a06" stroke-width="2"/>
  <circle cx="12" cy="12" r="6.6" fill="#ffd561" stroke="#b97f00" stroke-width="1.6"/>
  <path d="M9.6 8.8h4.8M12 8.8v6.6M9.6 15.4h4.8" stroke="#8a5f08" stroke-width="1.8" stroke-linecap="round"/>
`);

export const ELIXIR = svg('0 0 24 24', `
  <path d="M12 2c4.4 5.2 7 8.7 7 12a7 7 0 0 1-14 0c0-3.3 2.6-6.8 7-12z"
        fill="#d84fe0" stroke="#5c1c62" stroke-width="2" stroke-linejoin="round"/>
  <path d="M9 13c0 2 1.3 3.4 3 3.8" stroke="#ffd6fb" stroke-width="1.8" stroke-linecap="round" fill="none"/>
`);

export const DARK_ELIXIR = svg('0 0 24 24', `
  <path d="M12 2c4.4 5.2 7 8.7 7 12a7 7 0 0 1-14 0c0-3.3 2.6-6.8 7-12z"
        fill="#4a2a86" stroke="#1d0f3d" stroke-width="2" stroke-linejoin="round"/>
  <path d="M9 13c0 2 1.3 3.4 3 3.8" stroke="#b9a2f0" stroke-width="1.8" stroke-linecap="round" fill="none"/>
`);

export const TROPHY = svg('0 0 24 24', `
  <path d="M6 3h12v5a6 6 0 0 1-12 0z" fill="#ffd561" stroke="#7a5200" stroke-width="2" stroke-linejoin="round"/>
  <path d="M6 4.5H3.6v2A3.4 3.4 0 0 0 7 9.9M18 4.5h2.4v2A3.4 3.4 0 0 1 17 9.9"
        fill="none" stroke="#7a5200" stroke-width="2" stroke-linecap="round"/>
  <path d="M10.6 13.6h2.8l-.4 3.4h-2z" fill="#e0a500" stroke="#7a5200" stroke-width="1.6" stroke-linejoin="round"/>
  <rect x="7.4" y="17" width="9.2" height="3.4" rx="1.1" fill="#e39a12" stroke="#7a5200" stroke-width="2"/>
`);

export const RESOURCE_ICON = {
  GOLD,
  ELIXIR,
  DARK_ELIXIR
};

export const RESOURCE_LABEL = {
  GOLD: 'gold',
  ELIXIR: 'elixir',
  DARK_ELIXIR: 'dark elixir'
};
