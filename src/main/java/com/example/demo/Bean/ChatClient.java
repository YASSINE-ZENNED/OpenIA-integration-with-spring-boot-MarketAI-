package com.example.demo.Bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Component
public class ChatClient {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;


    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // Replace with actual API URL

    public ApiResponse call(String jsonRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(API_URL, entity, ApiResponse.class);

        return response.getBody();
    }

    public String sendPrompt(String prompt) {
        // Construct the request payload
        String requestBody = String.format(
                "{\"model\":\"gpt-4o\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":150}",
                prompt
        );

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Create HTTP entity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        // Extract and return the content from the response
        return extractContentFromResponse(response.getBody());
    }

    private String extractContentFromResponse(String responseBody) {
        // Parse the JSON response to extract the content
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAI response", e);
        }
    }
}
