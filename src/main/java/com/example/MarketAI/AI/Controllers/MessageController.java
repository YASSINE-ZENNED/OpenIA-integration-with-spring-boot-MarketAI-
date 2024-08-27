package com.example.MarketAI.AI.Controllers;


import com.example.MarketAI.AI.Models.Conversation;
import com.example.MarketAI.AI.Models.Message;
import com.example.MarketAI.AI.Models.MessageDTO;
import com.example.MarketAI.AI.Service.ConversationalContextServiceImpl;
import com.example.MarketAI.AI.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationalContextServiceImpl ConversationalContextServiceImpl;


    @GetMapping("/")
    public List<Message> getAllMessages() {

        return messageService.getAllMessages();

    }

    @GetMapping("/last10")
    public List<String> getLast10Messages(@RequestParam("id") Long id) {

        return messageService.getLast10Messages(id);

    }

    @PostMapping("/")
    public void sendMessage(@RequestBody MessageDTO message) {


        Message message1 = new Message();
        message1.setContent(message.getContent());
        message1.setPhotos(message.getPhotos());
        message1.setSender(message.getSender());
        message1.setTimestamp(message.getTimestamp());

        Conversation conversation = new Conversation();
        conversation = ConversationalContextServiceImpl.getConversation(0L).get();

        message1.setConversation(conversation);


        messageService.saveMessage(message1);


    }

}
