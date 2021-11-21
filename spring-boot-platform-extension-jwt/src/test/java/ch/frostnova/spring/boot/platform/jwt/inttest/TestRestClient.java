package ch.frostnova.spring.boot.platform.jwt.inttest;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;

@Component
public class TestRestClient {

    private RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    public String login(String baseURL, String tenant, String login, Set<String> roles, Map<String, String> additionalClaims) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        String url = String.format("%s/dev/login/%s/%s", baseURL, tenant, login);
        Map<String, String> queryParams = new HashMap<>();
        if (additionalClaims != null) {
            additionalClaims.forEach((k, v) -> queryParams.put(k, v));
        }
        if (roles != null && !roles.isEmpty()) {
            queryParams.put("roles", roles.stream().collect(joining(",")));
        }
        if (!queryParams.isEmpty()) {
            url += "?" + queryParams.keySet().stream().map(key -> String.format("%s=%s", key, queryParams.get(key))).collect(Collectors.joining("&"));
        }
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        expectStatus(response, 200);
        return response.getBody();
    }

    public UserInfo userInfo(String baseURL, String authorization) {
        return getResource(String.format("%s/user", baseURL), authorization, UserInfo.class);
    }

    private <T> T getResource(String url, String authorization, Class<T> type) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null) {
            headers.set("Authorization", authorization);
        }
        ResponseEntity<T> response = restTemplate.exchange(url, GET, new HttpEntity<>(null, headers), type);
        expectStatus(response, 200);
        return response.getBody();
    }

    private void expectStatus(ResponseEntity<?> response, int expectedStatus) {
        if (response.getStatusCodeValue() != expectedStatus) {
            throw new AssertionError("Expected response code " + expectedStatus + ", but got " + response.getStatusCodeValue());
        }
    }
}
