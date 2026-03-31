import { defineConfig } from '@playwright/test';

const playwrightPort = process.env.PLAYWRIGHT_PORT || '4200';
const baseUrl = `http://127.0.0.1:${playwrightPort}`;

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  timeout: 60_000,
  expect: {
    timeout: 15_000
  },
  use: {
    baseURL: baseUrl,
    browserName: 'chromium',
    headless: true,
    trace: 'on-first-retry'
  },
  webServer: {
    command: `npm start -- --host 127.0.0.1 --port ${playwrightPort}`,
    url: baseUrl,
    reuseExistingServer: process.env.PLAYWRIGHT_REUSE_SERVER !== '0',
    timeout: 120_000
  }
});
