package com.example.demo.Controllers;

import com.example.demo.Models.Item;
import com.example.demo.Service.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
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
import org.springframework.web.bind.annotation.PostMapping;
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
    @Autowired
    private OpenAiService openAiService;

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

        return openAiService.GenerateImage(prompt);

    }



    @PostMapping("/DescribeForClient")
    public Item describeImageCP(@RequestParam("image") MultipartFile imageFile, @RequestParam("keywords")  String keywords ) throws IOException {

        byte[] fileContent = imageFile.getBytes();
        return openAiService.describeImageAsUser(keywords, fileContent);

    }


    @PostMapping("/DescribeForEnterprise")
    public Item describeImageE(@RequestParam("image") MultipartFile imageFile, @RequestParam("keywords")  String keywords ) throws IOException {

        byte[] fileContent = imageFile.getBytes();
        return openAiService.describeImageAsEnterprise(keywords, fileContent);


    }




    }



