// angular-app/src/app/auth/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { from, switchMap } from 'rxjs';
import { getToken } from './keycloak.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Only attach token to your API calls
  if (!req.url.startsWith('/api/')) return next(req);

  return from(getToken()).pipe(
    switchMap((token) => {
      const authReq = token
        ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
        : req;

      return next(authReq);
    })
  );
};
