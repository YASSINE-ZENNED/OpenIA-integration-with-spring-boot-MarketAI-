package com.example.MarketAI.AI.Service;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConversationalContextServiceImpl implements ConversationalContextService {
    @Override
    public List<String> getConversationalContextsIds() {
        return List.of();
    }

    @Override
    public String prepareConversationalContext(String contextId, String message) {
        return "";
    }
}
