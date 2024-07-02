package com.example.demo.Controllers;

import com.example.demo.Models.Item;
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

    @GetMapping("/DescribeForClient")
    public String describeImageC(@RequestParam("image") MultipartFile imageFile, @RequestParam("keywords")  String keywords ) throws IOException {


        byte[] fileContent = imageFile.getBytes();

        SystemMessage systemMessage = new SystemMessage("I'd like to generate descriptions for items I'm selling on a second-hand marketplace. The items can be either new or used.  Can you create a template that sounds like a real person wrote it, not a big company?" +
                "I want the descriptions to be clear and honest about the item's condition add 3 bullet points of key features ."
        );

        UserMessage userMessage = new UserMessage(" use these words { " + keywords + " }",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG,fileContent)));

        var response = chatClient
                .call(new Prompt(List.of(systemMessage, userMessage)));
        return response.getResult().getOutput().getContent();

    }


    @PostMapping("/DescribeForClient")
    public Item describeImageCP(@RequestParam("image") MultipartFile imageFile, @RequestParam("keywords")  String keywords ) throws IOException {

        byte[] fileContent = imageFile.getBytes();

        var listOutputParser = new BeanOutputParser<>(Item.class);

        SystemMessage systemMessage = new SystemMessage("I'd like to generate descriptions for items I'm selling on a second-hand marketplace. The items can be either new or used.  Can you create a template that sounds like a real person wrote it, not a big company?" +
                "I want the descriptions to be clear and honest about the item's condition add 3 bullet points of key features if you cant tell the item tell to upload better photos of the product or if the item is not ok to sell just say sorry cant help you with this item dont use any name brand  for thr discription use 100 words."
                    +"here is the format i want you to use :" + listOutputParser.getFormat()
        );

        UserMessage userMessage = new UserMessage(" use these words { " + keywords + " }",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG,fileContent)));

        var response = chatClient.call(new Prompt(List.of(systemMessage, userMessage)));

        return listOutputParser.parse(response.getResult().getOutput().getContent());

    }


    @GetMapping("/DescribeForEnterprise")
    public Item describeImageE(@RequestParam("image") MultipartFile imageFile, @RequestParam("keywords")  String keywords ) throws IOException {
        byte[] fileContent = imageFile.getBytes();
        var listOutputParser = new BeanOutputParser<>(Item.class);
        SystemMessage systemMessage = new SystemMessage("Your primary function is to generate detailed and accurate descriptions of the main items in images provided by users for marketplace listings ."+"here is the format i want you to use :" + listOutputParser.getFormat());

        UserMessage userMessage = new UserMessage("give me  description of this image while using these words { " + keywords + " } and key features ",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG,fileContent)));

        var response = chatClient
                .call(new Prompt(List.of(systemMessage, userMessage)));
        return listOutputParser.parse(response.getResult().getOutput().getContent());
    }




    }



