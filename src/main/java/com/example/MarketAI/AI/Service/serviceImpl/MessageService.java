package com.example.MarketAI.AI.Service.serviceImpl;


import com.example.MarketAI.AI.Models.Message;
import com.example.MarketAI.AI.Models.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<String> getLast10Messages(Long id) {
        return messageRepository.findLast10ById(id);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
