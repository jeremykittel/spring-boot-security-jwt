package com.ninjasmoke.security.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class KeycloakRoleMappingSupport {

  private KeycloakRoleMappingSupport() {}

  static Set<String> readClientRoles(Map<String, Object> claims, String clientId) {
    return mapAt(claims, "resource_access")
        .flatMap(resourceAccess -> mapAt(resourceAccess, clientId))
        .flatMap(clientAccess -> collectionAt(clientAccess, "roles"))
        .map(KeycloakRoleMappingSupport::toStringSet)
        .orElseGet(Set::of);
  }

  private static Optional<Map<String, Object>> mapAt(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value instanceof Map<?, ?> m) {
      return Optional.of(unsafeCastToStringObjectMap(m));
    }
    return Optional.empty();
  }

  private static Optional<Collection<?>> collectionAt(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value instanceof Collection<?> c) {
      return Optional.of(c);
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> unsafeCastToStringObjectMap(Map<?, ?> map) {
    return (Map<String, Object>) map;
  }

  private static Set<String> toStringSet(Collection<?> raw) {
    Set<String> out = new HashSet<>();
    for (Object r : raw) {
      if (r != null) {
        out.add(r.toString());
      }
    }
    return out;
  }
}
