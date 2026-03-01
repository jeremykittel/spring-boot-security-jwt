// angular-app/src/app/auth/keycloak.service.ts
import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

// Keep a single Keycloak instance for the whole app
const keycloak = new Keycloak({
  url: 'http://app.local/auth', // Keycloak base URL
  realm: 'ninjasmoke',
  clientId: 'angular-spa', // <-- set to the SPA client you created
});

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  init(): Promise<boolean> {
    return keycloak.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false,
    });
  }

  async getToken(): Promise<string> {
    await keycloak.updateToken(30);
    return keycloak.token ?? '';
  }

  logout(): void {
    keycloak.logout({redirectUri: window.location.origin}).then(() => {
      console.log('Logged out successfully');
    });
  }
}
