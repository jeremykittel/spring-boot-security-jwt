package com.ninjasmoke.security.config;

import static com.ninjasmoke.security.config.KeycloakRoleMappingSupport.readClientRoles;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class KeycloakJwtRoleMappingConfig {

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter(
      @Value("${security.oauth2.client-id}") String clientId) {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        keycloakClientRolesJwtAuthoritiesConverter(clientId));
    return converter;
  }

  private Converter<Jwt, Collection<GrantedAuthority>> keycloakClientRolesJwtAuthoritiesConverter(
      String clientId) {
    return jwt -> {
      JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
      Set<GrantedAuthority> authorities = new HashSet<>(scopes.convert(jwt));

      readClientRoles(jwt.getClaims(), clientId).forEach(role -> {
        // Keep ROLE_ authorities for hasRole/hasAnyRole checks
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        // Also add a raw role for hasAuthority('admin:read') style permission checks
        authorities.add(new SimpleGrantedAuthority(role));
      });

      return authorities;
    };
  }
}
