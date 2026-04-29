package com.university.gateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aggregated Swagger Proxy Controller
 *
 * Serves each downstream microservice's OpenAPI spec through the gateway at:
 *   GET /v3/api-docs/{service}   (e.g. /v3/api-docs/identity)
 *
 * Before returning the spec it rewrites the "servers" block so that every
 * "Try it out" request in Swagger UI is routed through the gateway with the
 * correct service prefix:
 *   http://localhost:3000/identity   →   identity-service (port 3001)
 *   http://localhost:3000/catalog    →   catalog-service  (port 3002)
 *   ... and so on.
 *
 * The Swagger UI dropdown is driven by springdoc.swagger-ui.urls[] in
 * application.properties — one entry per service pointing to these endpoints.
 *
 * JWT auth filter already excludes any path containing /v3/api-docs, so
 * these endpoints are publicly accessible (the downstream specs themselves
 * are also public on each service).
 */
@RestController
public class SwaggerProxyController {

    // ── Service registry (order = display order in the Swagger UI dropdown) ──
    private static final Map<String, String> SERVICES = new LinkedHashMap<>();
    static {
        SERVICES.put("identity",      "http://localhost:3001");
        SERVICES.put("catalog",       "http://localhost:3002");
        SERVICES.put("enrollment",    "http://localhost:3003");
        SERVICES.put("assessment",    "http://localhost:3004");
        SERVICES.put("communication", "http://localhost:3005");
    }

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public SwaggerProxyController() {
        // Apache HttpClient 5: useSystemProperties() honours http.nonProxyHosts
        // so localhost calls are never routed through the corporate proxy.
        this.restTemplate = new RestTemplate(
                new HttpComponentsClientHttpRequestFactory(
                        HttpClientBuilder.create()
                                .useSystemProperties()
                                .build()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /v3/api-docs/{service}
    //  Returns the OpenAPI spec for the requested service with the server URL
    //  rewritten to point at the gateway (so Try-it-out goes through the GW).
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping(value = "/v3/api-docs/{service}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> serviceDocs(@PathVariable String service,
                                               HttpServletRequest request) {
        String baseUrl = SERVICES.get(service.toLowerCase());
        if (baseUrl == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String json = restTemplate.getForObject(baseUrl + "/v3/api-docs", String.class);
            if (json == null) {
                return unavailable(service, "Empty response from service");
            }

            // Parse the spec so we can rewrite the servers block
            JsonNode spec = mapper.readTree(json);
            ObjectNode mutable = spec.deepCopy();

            // Build gateway URL dynamically (works on any host / port)
            String gatewayBase = request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort();

            // Rewrite servers → single entry pointing at gateway/<prefix>
            // e.g.  http://localhost:3000/identity
            ArrayNode servers = mapper.createArrayNode();
            ObjectNode serverNode = servers.addObject();
            serverNode.put("url", gatewayBase + "/" + service.toLowerCase());
            serverNode.put("description", "API Gateway → " + capitalize(service) + " Service");
            mutable.set("servers", servers);

            // Ensure the spec carries a bearerAuth security scheme so the
            // Authorize button in the aggregated UI pre-populates correctly.
            ensureBearerAuth(mutable);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(mutable));

        } catch (Exception e) {
            return unavailable(service, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds a bearerAuth security scheme + global security requirement to the
     * spec if the downstream service did not already include one.
     */
    private void ensureBearerAuth(ObjectNode spec) {
        // Ensure components object exists
        ObjectNode components = (ObjectNode) spec.get("components");
        if (components == null) {
            components = mapper.createObjectNode();
            spec.set("components", components);
        }

        // Ensure securitySchemes object exists
        ObjectNode secSchemes = (ObjectNode) components.get("securitySchemes");
        if (secSchemes == null) {
            secSchemes = mapper.createObjectNode();
            components.set("securitySchemes", secSchemes);
        }

        // Add bearerAuth if not already present
        if (!secSchemes.has("bearerAuth")) {
            ObjectNode bearer = secSchemes.putObject("bearerAuth");
            bearer.put("type", "http");
            bearer.put("scheme", "bearer");
            bearer.put("bearerFormat", "JWT");
            bearer.put("description",
                    "JWT token obtained from POST /identity/auth/login. "
                    + "Paste the token value (without 'Bearer ') and click Authorize.");
        }

        // Add global security requirement if not already present
        if (!spec.has("security")) {
            ArrayNode security = spec.putArray("security");
            security.addObject().putArray("bearerAuth");
        }
    }

    private ResponseEntity<String> unavailable(String service, String message) {
        String body = String.format(
                "{\"error\":\"Service unavailable\",\"service\":\"%s\",\"message\":\"%s\"}",
                service,
                message == null ? "unknown error" : message.replace("\"", "'"));
        return ResponseEntity.status(503)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
