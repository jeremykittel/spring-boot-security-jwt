package com.ninjasmoke.security.config;

import static com.ninjasmoke.security.user.Permission.ADMIN_CREATE;
import static com.ninjasmoke.security.user.Permission.ADMIN_DELETE;
import static com.ninjasmoke.security.user.Permission.ADMIN_READ;
import static com.ninjasmoke.security.user.Permission.ADMIN_UPDATE;
import static com.ninjasmoke.security.user.Permission.MANAGER_CREATE;
import static com.ninjasmoke.security.user.Permission.MANAGER_DELETE;
import static com.ninjasmoke.security.user.Permission.MANAGER_READ;
import static com.ninjasmoke.security.user.Permission.MANAGER_UPDATE;
import static com.ninjasmoke.security.user.Role.ADMIN;
import static com.ninjasmoke.security.user.Role.MANAGER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private static final String[] SWAGGER_WHITE_LIST = {
    "/v2/api-docs",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui/**",
    "/webjars/**",
    "/swagger-ui.html"
  };

  private static final String[] CUSTOM_JWT_WHITE_LIST = {"/api/v1/auth/**"};

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final KeycloakOidcUserService keycloakOidcUserService;

  /**
   * ===========
   * Custom JWT mode (your current behavior)
   * ===========
   */
  @Bean
  @Order(100)
  @ConditionalOnProperty(
      prefix = "security",
      name = "auth-mode",
      havingValue = "custom-jwt",
      matchIfMissing = true)
  public SecurityFilterChain customJwtSecurityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(CUSTOM_JWT_WHITE_LIST)
                    .permitAll()
                    .requestMatchers(SWAGGER_WHITE_LIST)
                    .permitAll()
                    .requestMatchers("/api/v1/management/**")
                    .hasAnyRole(ADMIN.name(), MANAGER.name())
                    .requestMatchers(GET, "/api/v1/management/**")
                    .hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                    .requestMatchers(POST, "/api/v1/management/**")
                    .hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                    .requestMatchers(PUT, "/api/v1/management/**")
                    .hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                    .requestMatchers(DELETE, "/api/v1/management/**")
                    .hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(
                        (request, response, authentication) ->
                            SecurityContextHolder.clearContext()));

    return http.build();
  }

  /**
   * ===========
   * Keycloak mode - API: Bearer JWT (stateless)
   * ===========
   */
  @Bean
  @Order(10)
  @ConditionalOnProperty(prefix = "security", name = "auth-mode", havingValue = "keycloak")
  public SecurityFilterChain keycloakApiSecurityFilterChain(HttpSecurity http) {
    http.securityMatcher("/api/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(SWAGGER_WHITE_LIST)
                    .permitAll()
                    .requestMatchers("/api/v1/auth/**")
                    .denyAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

    return http.build();
  }

  /**
   * ===========
   * Keycloak mode - Web: login redirects (session)
   * ===========
   */
  @Bean
  @Order(20)
  @ConditionalOnProperty(prefix = "security", name = "auth-mode", havingValue = "keycloak")
  public SecurityFilterChain keycloakWebSecurityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(SWAGGER_WHITE_LIST)
                    .permitAll()
                    .requestMatchers("/api/v1/auth/**")
                    .denyAll()
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED))
        .oauth2Login(
            oauth2 ->
                oauth2.userInfoEndpoint(
                    userInfo -> userInfo.oidcUserService(keycloakOidcUserService)));

    return http.build();
  }
}
