package com.example.demo;

import com.example.demo.Service.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import groovy.util.logging.Slf4j;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChatController {

    @Autowired
    private OpenAiService openAiService;

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



//    @GetMapping("/Discribe")
//        public Object describeImage1(@RequestParam("image") MultipartFile imageFile) throws IOException{
//            // Check if the uploaded file is not empty and is an image
//            if (imageFile.isEmpty() ) {
//                throw new IllegalArgumentException("Please upload a valid image file.");
//            }
//        byte[] fileContent = imageFile.getBytes();
//        String encodedString =   Base64.getEncoder().encodeToString(fileContent);
//        System.out.println("Encoded String: " + encodedString);
//                Tika tika = new Tika();
//                String mimeType = tika.detect(fileContent);
//            // Convert MultipartFile to byte array
//
////        UserMessage userMessage = createUserMessage("Explain what do you see in this picture?", mimeType, encodedString);
//        var userMessage = new UserMessage("Explain what do you see on this picture?",
//                List.of(new Media(MimeTypeUtils.IMAGE_PNG, encodedString)));
//            // Create a UserMessage with the image data
//             Prompt prompt = new Prompt(List.of(userMessage),
//                OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());
//
//        System.out.println( "Prompt: " + prompt.toString()  );
//
//        ChatResponse response = chatModel.call(prompt);
//
//        // Example usage:
//        System.out.println("Response: " + response.getResult().getOutput().getContent());
//        // Build the prompt with the UserMessage and ChatModel options
//
//
//
//            // Return the response as a string (you may want to process the response further)
//            return response;
//        }
//
//    private static UserMessage createUserMessage(String message, String mimeType, String encodedString) {
//        // Here you would create or adapt the UserMessage constructor based on your UserMessage class implementation
//        // Replace with actual logic to create UserMessage
//        return new UserMessage(message);
//    }

    @GetMapping("/Media")
    public void describe(@RequestParam("image") MultipartFile imageFile) throws IOException {
        byte[] fileContent = imageFile.getBytes();

        String encodedString =   Base64.getEncoder().encodeToString(fileContent);
        Tika tika = new Tika();
        String mimeType = tika.detect(fileContent);

        openAiService.GetDiscribe(mimeType,encodedString);


    }

    }



