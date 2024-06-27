package com.example.demo.Controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeTypeUtils;

import groovy.util.logging.Slf4j;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChatController {



    @Autowired
    private OpenAiImageModel openAiImageModel;


    private final ChatModel chatModel;
    private final ChatClient chatClient;



    public ChatController( ChatModel chatModel , ChatClient.Builder chatClient) {
        this.chatModel = chatModel;
        this.chatClient = chatClient.build();

    }

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestParam(value = "prompt", defaultValue = "tell me a joke ") String prompt) {
        return Map.of("generation", chatModel.call(prompt));
    }

    @GetMapping("/Image")
    public String Gimage(@RequestParam(value = "prompt", defaultValue = "image of a man thinking oop i forgot to tell the AI what i want") String prompt) {

        ImageResponse response = openAiImageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024).build())

        );

        return response.toString();
    }

    @GetMapping("/image-describe")
    public String describeImage(@RequestParam("image") MultipartFile imageFile) throws IOException {

        byte[] fileContent = imageFile.getBytes();

        UserMessage userMessage = new UserMessage("Can you please explain what you see in the following image?",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG,fileContent)));
        var response = chatClient
                .call(new Prompt(userMessage));
        return response.getResult().getOutput().getContent();
    }




    }



