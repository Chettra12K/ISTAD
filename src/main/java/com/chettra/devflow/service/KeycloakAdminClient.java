
package com.chettra.devflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakAdminClient {

    private final RestClient client;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakAdminClient(
            @Value("${devflow.keycloak.server-url}") String serverUrl,
            @Value("${devflow.keycloak.realm}") String realm,
            @Value("${devflow.keycloak.admin-client-id}") String clientId,
            @Value("${devflow.keycloak.admin-client-secret}") String clientSecret
    ) {
        this.client = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String createUser(
            String username,
            String email,
            String firstname,
            String lastname,
            String initialPassword
    ) {
        Map<String, Object> representation = new LinkedHashMap<>();
        representation.put("username", username);
        representation.put("email", email);
        representation.put("enabled", true);
        representation.put("emailVerified", false);
        representation.put("firstName", firstname);
        representation.put("lastName", lastname);
        representation.put("credentials", List.of(Map.of(
                "type", "password",
                "value", initialPassword,
                "temporary", true
        )));

        var response = client.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(representation)
                .retrieve()
                .toBodilessEntity();

        URI location = response.getHeaders().getLocation();
        if (location == null || location.getPath() == null) {
            throw new IllegalStateException(
                    "Keycloak created a user but did not return its Location header"
            );
        }

        String path = location.getPath();
        String userId = path.substring(path.lastIndexOf('/') + 1);

        if (userId.isBlank()) {
            throw new IllegalStateException(
                    "Keycloak returned an invalid user Location header"
            );
        }

        return userId;
    }

    public void deleteUser(String keycloakId) {
        client.delete()
                .uri("/admin/realms/{realm}/users/{id}", realm, keycloakId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .toBodilessEntity();
    }

    private String bearerToken() {
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException(
                    "Keycloak provisioning client secret is not configured"
            );
        }

        LinkedMultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<?, ?> tokenResponse = client.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        if (tokenResponse == null
                || !(tokenResponse.get("access_token") instanceof String token)
                || token.isBlank()) {
            throw new IllegalStateException(
                    "Keycloak did not return an administrative access token"
            );
        }

        return "Bearer " + token;
    }
}
