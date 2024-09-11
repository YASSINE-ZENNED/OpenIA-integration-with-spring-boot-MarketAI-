package com.example.MarketAI.AI.Models;

import lombok.Data;


@Data
public class SearchRequestForAI {


    //    private static final String TAVILY_API_KEY = "TAVILY_API_KEY";
//
//    Dotenv dotenv = Dotenv.load();
//
//    private final String secretKey = dotenv.get(TAVILY_API_KEY);
    private final String api_key;
    private String query;

    // Constructor only takes query as argument
    public SearchRequestForAI(String query) {
        this.query = query;
        this.api_key = "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";

    }

}