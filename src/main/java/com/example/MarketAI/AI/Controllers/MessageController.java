package com.example.MarketAI.AI.Controllers;


import com.example.MarketAI.AI.Models.Message;
import com.example.MarketAI.AI.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;


    @GetMapping("/")
    public List<Message> getAllMessages() {

        return messageService.getAllMessages();

    }

    @PostMapping("/")
    public void sendMessage(@RequestBody Message message) {


        messageService.saveMessage(message);


    }

}
