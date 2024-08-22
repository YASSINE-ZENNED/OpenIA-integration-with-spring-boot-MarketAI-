package com.example.MarketAI.AI.Controllers;

import com.example.MarketAI.AI.Models.Conversation;
import com.example.MarketAI.AI.Models.MessageDTO;
import com.example.MarketAI.AI.Service.ConversationalContextServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationalContextServiceImpl conversationalContextService;


    @GetMapping("/")
    public List<Long> getConversationalContexts() {
        return conversationalContextService.getConversationalContextsIds();
    }

    @GetMapping("/getConversation")
    public Optional<Conversation> getConversational(@RequestParam("ConversationID") Long contextId) {
        System.out.println("contextId = " + contextId);
        return conversationalContextService.getConversation(contextId);

    }

    @PostMapping("/preparePrompt")
    public String preparePromptConversationalContext(@RequestBody MessageDTO messageDTO) {


        return conversationalContextService.preparePromptConversationalContext(messageDTO);

    }

}
