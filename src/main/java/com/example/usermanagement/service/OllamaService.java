package com.example.usermanagement.service;

import com.example.usermanagement.dto.AiDtos.AiQueryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class OllamaService {
    private final RestClient client;
    private final String model;

    public OllamaService(RestClient ollamaRestClient, @Value("${ollama.model}") String model) {
        this.client = ollamaRestClient;
        this.model = model;
    }

    public AiQueryResponse query(String prompt) {
        try {
            Map<?, ?> result = client.post().uri("/api/generate").contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", model, "prompt", prompt, "stream", false))
                    .retrieve().body(Map.class);
            if (result == null || result.get("response") == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Ollama returned an empty response");
            }
            return new AiQueryResponse(model, String.valueOf(result.get("response")));
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Ollama is unavailable or timed out; start Ollama and check the model.");
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Ollama request failed (HTTP " + e.getStatusCode().value() + "). Is model '" + model + "' pulled?");
        }
    }
}
