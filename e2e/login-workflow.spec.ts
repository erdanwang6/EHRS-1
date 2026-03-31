import { expect, test } from '@playwright/test';

test('login screen authenticates a doctor and routes to the landing page', async ({ page }) => {
  await page.goto('/login');

  const usernameInput = page.locator('input[formcontrolname="username"]');
  const passwordInput = page.locator('input[formcontrolname="password"]');
  const passwordToggle = page.getByRole('button', { name: 'Hide password' });

  await expect(page.locator('mat-card-title')).toHaveText('LOG IN');
  await expect(usernameInput).toBeVisible();
  await expect(passwordInput).toHaveAttribute('type', 'password');

  await usernameInput.fill('dr.chen');
  await passwordInput.fill('Doctor123!');

  await passwordToggle.click();
  await expect(passwordInput).toHaveAttribute('type', 'text');

  await page.getByRole('button', { name: 'LOG IN' }).click();

  await expect(page).toHaveURL(/\/home\/doctorlanding/);
  await expect(page.getByRole('heading', { name: 'Doctor Landing Page' })).toBeVisible();
  await expect(page.getByTestId('header-user')).toContainText('Olivia Chen');
});

test('login screen shows a validation error for invalid credentials', async ({ page }) => {
  await page.goto('/login');

  await page.locator('input[formcontrolname="username"]').fill('dr.chen');
  await page.locator('input[formcontrolname="password"]').fill('WrongPassword!');
  await page.getByRole('button', { name: 'LOG IN' }).click();

  await expect(page).toHaveURL(/\/login/);
  await expect(page.getByTestId('login-error')).toContainText('Invalid username or password');
});
