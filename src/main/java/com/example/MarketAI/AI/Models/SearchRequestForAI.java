package com.example.MarketAI.AI.Models;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;


public class SearchRequestForAI {

//    Dotenv dotenv = Dotenv.load();
//
//    // Access the variables
//    String secretKey = dotenv.get("TAVILY_API_KEY");


    @Value("${spring.tavily.api-key}")
    private String api_key;

    @Setter
    private String query;

    // Constructor only takes query as argument
    public SearchRequestForAI(String query) {
        this.query = query;
        this.api_key = api_key;

    }

}