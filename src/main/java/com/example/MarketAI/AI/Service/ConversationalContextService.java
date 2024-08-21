package com.example.MarketAI.AI.Service;

import com.example.MarketAI.AI.Models.MessageDTO;

import java.util.List;

public interface ConversationalContextService {
    List<Long> getConversationalContextsIds();

    String preparePromptConversationalContext(MessageDTO messageDTO);

}
