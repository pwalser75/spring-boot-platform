package ch.frostnova.spring.boot.platform.core.inttest;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@Component
public class TestRestClient {

    private RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    public UserInfo userInfo(String baseURL, String authorization) {
        return getResource(String.format("%s/user", baseURL), authorization, UserInfo.class);
    }

    public String hello(String baseURL, String authorization) {
        return getResource(String.format("%s/hello", baseURL), authorization, String.class);
    }

    public String publicResource(String baseURL, String authorization) {
        return getResource(String.format("%s/access/public", baseURL), authorization, String.class);
    }

    public String privateResource(String baseURL, String authorization) {
        return getResource(String.format("%s/access/private", baseURL), authorization, String.class);
    }

    public String adminResource(String baseURL, String authorization) {
        return getResource(String.format("%s/access/admin", baseURL), authorization, String.class);
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
