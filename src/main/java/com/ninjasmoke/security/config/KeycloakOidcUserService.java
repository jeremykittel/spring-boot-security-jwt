package com.ninjasmoke.security.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class KeycloakOidcUserService extends OidcUserService {

  private final String clientId;
  private final JwtDecoder jwtDecoder;

  public KeycloakOidcUserService(
      JwtDecoder jwtDecoder, @Value("${security.oauth2.client-id}") String clientId) {
    this.jwtDecoder = jwtDecoder;
    this.clientId = clientId;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) {
    OidcUser delegate = super.loadUser(userRequest);

    Set<GrantedAuthority> mapped = new HashSet<>(delegate.getAuthorities());

    // 1) ID token claims
    mapped.addAll(
        toRoleAuthorities(
            KeycloakRoleMappingSupport.readClientRoles(
                delegate.getIdToken().getClaims(), clientId)));

    // 2) UserInfo claims (if Keycloak provides it)
    OidcUserInfo userInfo = delegate.getUserInfo();
    if (userInfo != null) {
      mapped.addAll(
          toRoleAuthorities(
              KeycloakRoleMappingSupport.readClientRoles(userInfo.getClaims(), clientId)));
    }

    // 3) Access token claims (decode JWT access token)
    String tokenValue = userRequest.getAccessToken().getTokenValue();
    Jwt accessTokenJwt = jwtDecoder.decode(tokenValue);
    mapped.addAll(
        toRoleAuthorities(
            KeycloakRoleMappingSupport.readClientRoles(accessTokenJwt.getClaims(), clientId)));

    // Use preferred_username as the “name” attribute if present, otherwise keep default
    String nameAttr = "preferred_username";
    Map<String, Object> idClaims = delegate.getIdToken().getClaims();
    if (!idClaims.containsKey(nameAttr)) {
      nameAttr = delegate.getName();
      // Note: DefaultOidcUser requires a claim name, not the computed name;
      // if preferred_username is missing, "sub" is a safe fallback.
      nameAttr = "sub";
    }

    return new DefaultOidcUser(mapped, delegate.getIdToken(), delegate.getUserInfo(), nameAttr);
  }

  private static Collection<? extends GrantedAuthority> toRoleAuthorities(Set<String> roles) {
    Set<GrantedAuthority> out = new HashSet<>();
    for (String role : roles) {
      out.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    return out;
  }
}
