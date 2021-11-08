package ch.frostnova.spring.boot.platform.jwt.rest;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.jwt.rest.interceptor.LoggingInterceptor;
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
        Map<String, String> queryParmams = new HashMap<>();
        if (additionalClaims != null) {
            additionalClaims.forEach((k, v) -> queryParmams.put(k, v));
        }
        if (roles != null && !roles.isEmpty()) {
            queryParmams.put("roles", roles.stream().collect(joining(",")));
        }
        if (!queryParmams.isEmpty()) {
            url += "?" + queryParmams.keySet().stream().map(key -> String.format("%s=%s", key, queryParmams.get(key))).collect(Collectors.joining("&"));
        }
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        expectStatus(response, 200);
        return response.getBody();
    }

    public UserInfo userInfo(String baseURL, String jwt) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        String url = String.format("%s/dev/user", baseURL);

        HttpHeaders headers = new HttpHeaders();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        ResponseEntity<UserInfo> response = restTemplate.exchange(url, GET, new HttpEntity<>(null, headers), UserInfo.class);
        expectStatus(response, 200);
        return response.getBody();
    }

    public String hello(String baseURL, String jwt) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        String url = String.format("%s/hello", baseURL);

        HttpHeaders headers = new HttpHeaders();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        ResponseEntity<String> response = restTemplate.exchange(url, GET, new HttpEntity<>(null, headers), String.class);
        expectStatus(response, 200);
        return response.getBody();
    }

    private void expectStatus(ResponseEntity<?> response, int expectedStatus) {
        if (response.getStatusCodeValue() != expectedStatus) {
            throw new AssertionError("Expected response code " + expectedStatus + ", but got " + response.getStatusCodeValue());
        }
    }
}
