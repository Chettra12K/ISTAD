package com.chettra.devflow.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeycloakJwtGrantedAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object roles = realmAccess.get("roles");

        if (!(roles instanceof Collection<?> roleCollection)) {
            return Collections.emptyList();
        }

        return roleCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role ->
                        (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role)
                )
                .toList();
    }
}