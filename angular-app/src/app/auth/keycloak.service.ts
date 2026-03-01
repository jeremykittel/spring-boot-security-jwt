// angular-app/src/app/auth/keycloak.service.ts
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://app.local/auth', // Keycloak base URL
  realm: 'ninjasmoke',
  clientId: 'angular-spa', // <-- set to the SPA client you created
});

export async function initKeycloak(): Promise<boolean> {
  return keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  });
}

export async function getToken(): Promise<string> {
  await keycloak.updateToken(30);
  return keycloak.token ?? '';
}

export function logout(): void {
  keycloak.logout({ redirectUri: window.location.origin });
}
