package com.nocountry.churninsight_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class ChurnService {

    private final RestTemplate restTemplate;
    private final String dsServiceUrl;

    public ChurnService(
            RestTemplateBuilder builder,
            @Value("${ds.service.url:}") String dsServiceUrl
    ) {
        this.dsServiceUrl = dsServiceUrl == null ? "" : dsServiceUrl.trim();
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    public PythonProxyResult chamarPredict(Map<String, Object> payload) {
        return exchangeJson(HttpMethod.POST, "/predict", payload);
    }

    public PythonProxyResult chamarHealth() {
        return exchangeJson(HttpMethod.GET, "/health", null);
    }

    public PythonProxyResult chamarDemoExamples() {
        return exchangeJson(HttpMethod.GET, "/demo-examples", null);
    }

    private PythonProxyResult exchangeJson(HttpMethod method, String path, Object bodyObj) {
        if (dsServiceUrl.isBlank()) {
            return new PythonProxyResult(
                    500,
                    "{\"detail\":\"ds.service.url não configurado\"}",
                    0L,
                    ""
            );
        }

        String base = dsServiceUrl;
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String normalizedPath = (path == null || path.isBlank())
                ? ""
                : (path.startsWith("/") ? path : "/" + path);

        String url = UriComponentsBuilder
                .fromUriString(base)
                .path(normalizedPath)
                .build()
                .toUriString();

        long t0 = System.nanoTime();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // Só define Content-Type quando existe body (ex.: POST)
            if (bodyObj != null) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            HttpEntity<?> entity = (bodyObj == null)
                    ? new HttpEntity<>(headers)
                    : new HttpEntity<>(bodyObj, headers);

            ResponseEntity<String> resp = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    String.class
            );

            long latencyMs = (System.nanoTime() - t0) / 1_000_000L;
            String body = resp.getBody() == null ? "" : resp.getBody();

            return new PythonProxyResult(resp.getStatusCode().value(), body, latencyMs, url);

        } catch (HttpStatusCodeException e) {
            long latencyMs = (System.nanoTime() - t0) / 1_000_000L;
            String body = e.getResponseBodyAsString();
            return new PythonProxyResult(
                    e.getStatusCode().value(),
                    body == null ? "" : body,
                    latencyMs,
                    url
            );

        } catch (RestClientException e) {
            long latencyMs = (System.nanoTime() - t0) / 1_000_000L;
            String msg = "{\"detail\":\"Falha ao chamar FastAPI em " + url + ": " + escapeForJson(e.getMessage()) + "\"}";
            return new PythonProxyResult(502, msg, latencyMs, url);
        }
    }

    // Evita quebrar JSON quando a exception contém aspas/backslashes
    private static String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public record PythonProxyResult(int status, String body, long latencyMs, String targetUrl) {}
}
