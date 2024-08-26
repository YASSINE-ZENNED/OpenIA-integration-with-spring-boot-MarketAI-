package com.example.MarketAI.AI.Service;

import com.example.MarketAI.AI.Models.Conversation;
import com.example.MarketAI.AI.Models.ConversationRepository;
import com.example.MarketAI.AI.Models.Message;
import com.example.MarketAI.AI.Models.MessageDTO;
import com.example.MarketAI.AI.PromptConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.MarketAI.AI.Models.Sender.USER;


@Service
public class ConversationalContextServiceImpl implements ConversationalContextService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageService messageService;

    public Optional<Conversation> getConversation(Long contextId) {

        return conversationRepository.findById(contextId);
    }

    @Override
    public List<Long> getConversationalContextsIds() {

        return conversationRepository.findAll().stream().map(Conversation::getId).toList();
    }

    @Override
    public String preparePromptConversationalContext(MessageDTO messageDTO) {


        if (messageDTO.getConversationID() == null) {
            System.out.println("messageDTO = " + messageDTO);
            return "Conversation ID is missing";
        }

        if (conversationRepository.findById(messageDTO.getConversationID()).isEmpty()) {
            System.out.println("_________________________");
            System.out.println("we good inside");
            System.out.println("_________________________");

            Conversation conversation = new Conversation();

            conversation.setId((messageDTO.getConversationID()));

            LocalDate currentDate = LocalDate.now();


            conversation.setDateOfLastMessage(currentDate);

            conversationRepository.saveAndFlush(conversation);


            Message message1 = new Message();
            message1.setConversation(conversation);

            message1.setContent(messageDTO.getContent());
            message1.setTimestamp(System.currentTimeMillis());
            message1.setPhotos(messageDTO.getPhotos());
            message1.setSender(USER);

            messageService.saveMessage(message1);
            return "Start a new conversation";

        } else {
            Conversation conversation = conversationRepository.findById(messageDTO.getConversationID()).orElseThrow(() -> new RuntimeException("conversation not found"));
            ;


            Message message1 = new Message();
            message1.setConversation(conversation);

            message1.setContent(messageDTO.getContent());
            message1.setTimestamp(System.currentTimeMillis());
            message1.setPhotos(messageDTO.getPhotos());
            message1.setSender(USER);


            messageService.saveMessage(message1);

//            MessageService.getLast10Messages(conversation.getId());

            StringBuilder prompt = new StringBuilder();

            prompt.append(PromptConstants.PROMPT_WHAT_WERE_WE_TALKING_ABOUT);

            return "Continue the conversation Message ADDED";
        }


    }
}
