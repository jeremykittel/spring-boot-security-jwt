package com.ninjasmoke.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info =
        @Info(
                contact =
                @Contact(
                        name = "Ninja",
                        email = "contact@ninjasmoke.com",
                        url = "https://ninjasmoke.com/course"),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - Ninja",
                version = "1.0",
                license = @License(name = "Licence name", url = "https://some-url.com"),
                termsOfService = "Terms of service"),
        security = {@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {}