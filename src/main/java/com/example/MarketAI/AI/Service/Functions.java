package com.example.MarketAI.AI.Service;


import com.example.MarketAI.AI.Models.SearchRequestForAI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;

@Configuration
public class Functions {


    public record Request(String itemName, String itemStatus, String itemBrand) {
    }

    @Bean
    @Description("Get the average price of this item based on the status and brand")
    public Function<Request, List<String>> getPrices() {

        System.out.println("its fucked in here ********************************************");
        System.out.println("its fucked in here ********************************************");
        System.out.println("its fucked in here ********************************************");
        System.out.println("its fucked in here ********************************************");
        System.out.println("its fucked in here ********************************************");

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
}
