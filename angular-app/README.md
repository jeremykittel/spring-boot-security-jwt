# AngularApp

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.0.

## Development server

To start a local development server, run:

```bash
npm start
```

Once the server is running, open your browser and navigate to `https://app.local:4200/`. The application will automatically reload whenever you modify any of the source files.

### HTTPS on `app.local` (required for Keycloak PKCE)

`keycloak-js` uses the Web Crypto API for PKCE. Browsers only expose Web Crypto in a **secure context**:
- `https://…` ✅
- `http://localhost…` ✅ (special-case)
- `http://app.local…` ❌

So for local dev on `app.local`, run Angular on **HTTPS**.

#### 1) Map `app.local` to localhost

Add this to `/etc/hosts`:

```text
127.0.0.1 app.local
```

#### 2) Install mkcert (macOS)

```bash
brew install mkcert
brew install nss # optional; helpful for Firefox
mkcert -install
```

#### 3) Generate a local certificate for `app.local`

From the Angular project folder:

```bash
cd angular-app
mkdir -p .cert
mkcert -key-file .cert/app.local-key.pem -cert-file .cert/app.local.pem app.local
```

This creates:
- `angular-app/.cert/app.local.pem`
- `angular-app/.cert/app.local-key.pem`

> Note: `.cert/` is local-dev only. Do not commit the private key.

#### 4) Keycloak SPA client settings

In Keycloak, the SPA client must include:

- **Valid redirect URIs**: `https://app.local:4200/*`
- **Web origins**: `https://app.local:4200`

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
```
