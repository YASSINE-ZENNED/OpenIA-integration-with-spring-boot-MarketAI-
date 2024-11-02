package com.example.MarketAI.AI.Service.serviceImpl;

import com.example.MarketAI.AI.Config.PromptConstants;
import com.example.MarketAI.AI.Models.Conversation;
import com.example.MarketAI.AI.Models.ConversationRepository;
import com.example.MarketAI.AI.Models.Message;
import com.example.MarketAI.AI.Models.MessageDTO;
import com.example.MarketAI.AI.Service.ConversationalContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.MarketAI.AI.Models.Sender.BOT;
import static com.example.MarketAI.AI.Models.Sender.USER;


@Service
public class ConversationalContextServiceImpl implements ConversationalContextService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    OpenAiService openAiService;


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
            System.out.println("We good inside");
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

            String Response = openAiService.SupportChatWithImage(messageDTO.getContent(), null);

            Message Responsemessage = new Message();
            Responsemessage.setConversation(conversation);

            Responsemessage.setContent(Response);
            Responsemessage.setTimestamp(System.currentTimeMillis());
            Responsemessage.setSender(BOT);

            messageService.saveMessage(Responsemessage);
            return Response;

        } else {
            Conversation conversation = conversationRepository.findById(messageDTO.getConversationID()).orElseThrow(() -> new RuntimeException("conversation not found"));

            Message message1 = new Message();
            message1.setConversation(conversation);

            message1.setContent(messageDTO.getContent());
            message1.setTimestamp(System.currentTimeMillis());
            message1.setPhotos(messageDTO.getPhotos());
            message1.setSender(USER);

            messageService.saveMessage(message1);

            List<String> MessageHistory = messageService.getLast10Messages(conversation.getId());

            StringBuilder prompt = new StringBuilder();

            prompt.append(PromptConstants.PROMPT_WHAT_WERE_WE_TALKING_ABOUT);

            for (String message : MessageHistory) {
                prompt.append(PromptConstants.PROMPT_DELIMITER + message + PromptConstants.PROMPT_DELIMITER);
            }
            prompt.append(PromptConstants.PROMPT_DELIMITER_FOR_HISTORICAL_CONTEXT);
            prompt.append(PromptConstants.PROMPT_USE_CONTEXT_IF_NEEDED);
            prompt.append(PromptConstants.PROMPT_THE_CURRENT_QUESTION);
            prompt.append(message1.getContent());

            String Response = openAiService.SupportChatWithImage(String.valueOf(prompt), null);

            Message Responsemessage = new Message();
            Responsemessage.setConversation(conversation);

            Responsemessage.setContent(Response);
            Responsemessage.setTimestamp(System.currentTimeMillis());
            Responsemessage.setSender(BOT);

            messageService.saveMessage(Responsemessage);


            return Response;
        }
    }
}
