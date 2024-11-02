package com.example.MarketAI.AI.Service;


import com.example.MarketAI.AI.Models.SearchRequestForAI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;

@Configuration
public class Functions {


    public record Request(String itemName, String itemStatus, String itemBrand, String itemModel) {
    }

    @Bean
    @Description("Get the average price of this item based on the status and brand")
    public Function<Request, List<String>> getPrices() {

        return (request) -> {

            RestTemplate restTemplate = new RestTemplate();
            System.out.println("request = " + request);
            String query = String.format(
                    "give me the average price of %s based on the status %s and brand %s",
                    request.itemName(), request.itemStatus(), request.itemBrand()
            );
            SearchRequestForAI searchRequestForAI = new SearchRequestForAI(query);
            // Set the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Create an HTTP entity with the request and headers
            HttpEntity<SearchRequestForAI> entity = new HttpEntity<>(searchRequestForAI, headers);


            // Execute the POST request
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tavily.com/search", entity, String.class
            );
            // Process the response
            String responseBody = response.getBody();
            System.out.println("responseBody = " + responseBody);

            // Assuming responseBody contains prices in some list format, parse it
            // In this case, you can parse JSON to extract prices
            return parsePricesFromResponse(responseBody);
        };
    }

    // Assuming a helper function to parse prices from the response body
    private List<String> parsePricesFromResponse(String responseBody) {
        // Implement the logic to extract prices from the response
        // This would depend on the API's response structure
        // Placeholder logic below:
        return List.of(responseBody); // Replace with actual parsing logic
    }

    @Bean
    @Description("Search for exact item model and return detailed technical info")
    public Function<Request, ItemDetails> getItemDetails() {

        return (request) -> {
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("Request received: " + request);

            // Formulate a search query for item details
            String query = String.format(
                    "Provide detailed technical information and price for model %s, brand %s, status %s",
                    request.itemModel(), request.itemBrand(), request.itemStatus()
            );
            SearchRequestForAI searchRequestForAI = new SearchRequestForAI(query);

            // Set the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create an HTTP entity with the request and headers
            HttpEntity<SearchRequestForAI> entity = new HttpEntity<>(searchRequestForAI, headers);

            try {
                // Execute the POST request
                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://api.tavily.com/search", entity, String.class
                );

                // Process the response
                String responseBody = response.getBody();
                System.out.println("Response Body: " + responseBody);

                // Parse the technical information and prices from the response
                return parseItemDetailsFromResponse(responseBody);

            } catch (RestClientException e) {
                System.err.println("Error occurred during API call: " + e.getMessage());
                return null; // Handle the error or return a default value
            }
        };
    }

    // Define a helper class to hold item details
    public class ItemDetails {
        private String technicalInfo;
        private String averagePrice;
        private List<String> features;

        public ItemDetails(String technicalInfo, String averagePrice, List<String> features) {
            this.technicalInfo = technicalInfo;
            this.averagePrice = averagePrice;
            this.features = features;

        }

        // Constructor, getters, and setters
    }

    // Assuming a helper function to parse item details from the response body
    private ItemDetails parseItemDetailsFromResponse(String responseBody) {
        // Implement the JSON parsing logic to extract details
        // Placeholder logic below:
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(responseBody);

            // Extract technical information, average price, and features
            String technicalInfo = jsonNode.path("technicalInfo").asText();
            String averagePrice = jsonNode.path("averagePrice").asText();
            List<String> features = mapper.convertValue(
                    jsonNode.path("features"), new TypeReference<List<String>>() {
                    });

            return new ItemDetails(technicalInfo, averagePrice, features);

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing response: " + e.getMessage());
            return null;
        }
    }

}
