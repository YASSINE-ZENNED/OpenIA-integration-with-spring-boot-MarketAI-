package com.example.MarketAI.AI.Config;


public class PromptConstants {
    public static final String PROMPT_DELIMITER = " <||> ";
    public static final String PROMPT_WHAT_WERE_WE_TALKING_ABOUT = "We have been talking about these things as of now. these the last messages we have exchanged :";
    public static final String PROMPT_DELIMITER_FOR_HISTORICAL_CONTEXT = "Please note that the previous queries are delimited " +
            "by the string" + PROMPT_DELIMITER;
    public static final String PROMPT_USE_CONTEXT_IF_NEEDED = "Make use of the contextual information about things discussed " +
            "till now only if it's super necessary to answer the current question, Otherwise ignore the historical discussion so far" +
            " and answer or acknowledge from your general wisdom! Also don't include " + PROMPT_DELIMITER + " in your response!";

    public static final String PROMPT_THE_CURRENT_QUESTION = "Now here is your current question :";

    private PromptConstants() {
    }
}