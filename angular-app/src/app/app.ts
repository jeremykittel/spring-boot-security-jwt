import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { KeycloakService } from './auth/keycloak.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatButtonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly keycloak = inject(KeycloakService);

  protected readonly title = signal('angular-app');

  logout(): void {
    this.keycloak.logout();
  }
}
