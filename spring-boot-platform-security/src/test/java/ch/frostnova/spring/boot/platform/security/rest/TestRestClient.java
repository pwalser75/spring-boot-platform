package ch.frostnova.spring.boot.platform.security.rest;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.rest.interceptor.LoggingInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;

@Component
public class TestRestClient {

    private RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .additionalInterceptors(List.of(new LoggingInterceptor()));
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

    public UserInfo userInfo(String baseURL, String jwt) {
        return getResource(String.format("%s/dev/user", baseURL), jwt, UserInfo.class);
    }

    public String hello(String baseURL, String jwt) {
        return getResource(String.format("%s/hello", baseURL), jwt, String.class);
    }

    public String publicResource(String baseURL, String jwt) {
        return getResource(String.format("%s/access/public", baseURL), jwt, String.class);
    }

    public String privateResource(String baseURL, String jwt) {
        return getResource(String.format("%s/access/private", baseURL), jwt, String.class);
    }

    public String adminResource(String baseURL, String jwt) {
        return getResource(String.format("%s/access/admin", baseURL), jwt, String.class);
    }

    private <T> T getResource(String url, String jwt, Class<T> type) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
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
