package com.example.MarketAI.AI.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SearchRequestForAI {

    @JsonIgnore
    Dotenv dotenv = Dotenv.load();


    private String api_key;

    private String query;

    // Constructor only takes query as argument
    public SearchRequestForAI(String query) {

        this.query = query;
        this.api_key = dotenv.get("TAVILY_API_KEY");

    }

}