package com.ninjasmoke.security.auditing;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class ApplicationAuditAware implements AuditorAware<String> {
  @Override
  public @NonNull Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }

    Object principal = authentication.getPrincipal();

    // Keycloak (resource server): JwtAuthenticationToken
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      return Optional.ofNullable(jwtAuth.getToken().getSubject());
    }

    // Keycloak (oauth2Login): OidcUser principal
    if (principal instanceof OidcUser oidcUser) {
      return Optional.ofNullable(oidcUser.getSubject());
    }

    // Fallback if principal is directly a Jwt
    if (principal instanceof Jwt jwt) {
      return Optional.ofNullable(jwt.getSubject());
    }

    // Fallback for other authentication types
    if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
      return Optional.of(ud.getUsername());
    }
    if (principal instanceof String s) {
      return Optional.of(s);
    }

    return Optional.empty();
  }
}
