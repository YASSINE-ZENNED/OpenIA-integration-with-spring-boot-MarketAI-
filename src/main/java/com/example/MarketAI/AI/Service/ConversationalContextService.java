package com.example.MarketAI.AI.Service;

import java.util.List;

public interface ConversationalContextService {
    List<String> getConversationalContextsIds();
    String  prepareConversationalContext(final String contextId, String message);

}
