package com.nocountry.churninsight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocountry.churninsight_backend.service.ChurnService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/churn")
public class ChurnController {

    private final ChurnService churnService;
    private final ObjectMapper objectMapper;
    private final String dsServiceUrl;

    public ChurnController(
            ChurnService churnService,
            ObjectMapper objectMapper,
            @Value("${ds.service.url:}") String dsServiceUrl
    ) {
        this.churnService = churnService;
        this.objectMapper = objectMapper;
        this.dsServiceUrl = dsServiceUrl == null ? "" : dsServiceUrl.trim();
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> health() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("status", "ok");
        out.put("ds_service_url", dsServiceUrl);
        return out;
    }

    @GetMapping(value = "/ds-health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dsHealth() {
        return proxyJson(churnService.chamarHealth(), true);
    }

    @GetMapping(value = "/demo-examples", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> demoExamples() {
        return proxyJson(churnService.chamarDemoExamples(), true);
    }

    @PostMapping(value = "/predict", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> predict(@RequestBody Map<String, Object> payload) {
        return proxyJson(churnService.chamarPredict(payload), false);
    }

    private ResponseEntity<?> proxyJson(ChurnService.PythonProxyResult result, boolean includeDsUrlOnFallback) {
        int status = result.status() > 0 ? result.status() : 502;
        String body = (result.body() == null) ? "" : result.body();

        HttpHeaders outHeaders = new HttpHeaders();
        outHeaders.setContentType(MediaType.APPLICATION_JSON);
        outHeaders.add("X-Proxy-Latency-Ms", String.valueOf(result.latencyMs()));
        if (result.targetUrl() != null && !result.targetUrl().isBlank()) {
            outHeaders.add("X-Target-Url", result.targetUrl());
        }

        try {
            Object asJson = objectMapper.readValue(body, Object.class);
            return new ResponseEntity<>(asJson, outHeaders, HttpStatus.valueOf(status));
        } catch (Exception ignore) {
            Map<String, Object> wrap = new LinkedHashMap<>();
            wrap.put("detail", body);
            if (includeDsUrlOnFallback) {
                wrap.put("ds_service_url", dsServiceUrl);
            }
            return new ResponseEntity<>(wrap, outHeaders, HttpStatus.valueOf(status));
        }
    }
}
