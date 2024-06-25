package com.example.demo.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void GetDiscribe(String mimeType, String base64Image) {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Prepare payload
        ChatPayload payload = new ChatPayload();
        payload.setModel("gpt-4o");

        ChatMessage message = new ChatMessage();
        message.setRole("user");

        TextContent textContent = new TextContent();
        textContent.setType("text");
        textContent.setText("What’s in this image?");


        ImageContent imageContent = new ImageContent();
        imageContent.setType("image_url");


        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl("data:" + mimeType + ";base64," + base64Image);

         Object content = imageUrl;


        imageContent.setImage_url(content);

        System.out.println(imageContent.getImage_url());

        String imageson1 = convertObjectToJson(imageContent);

        System.out.println("555555555555555: " + imageson1);


        message.setContent(List.of(textContent, imageContent));



        payload.setMessages(List.of(message));
//        payload.setMaxTokens(300);

        // Convert payload to JSON string for display
        String payloadJson = convertObjectToJson(payload);
        System.out.println("Payload: " + payloadJson);

        // Make API call
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        HttpEntity<ChatPayload> requestEntity = new HttpEntity<>(payload);
        ChatResponse response = restTemplate.postForObject(apiUrl, requestEntity, ChatResponse.class);

        String result = convertObjectToJson(response);

        System.out.println("Result: " + result);
        // Handle response as needed
        System.out.println("Response: " + response.getClass().getName());
    }

    private String convertObjectToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Inner classes for payload structure

    static class ChatPayload {
        private String model;
        private List<ChatMessage> messages;
//        private int maxTokens;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

//        public int getMaxTokens() {
//            return maxTokens;
//        }
//
//        public void setMaxTokens(int maxTokens) {
//            this.maxTokens = maxTokens;
//        }

        public List<ChatMessage> getMessages() {
            return messages;
        }

        public void setMessages(List<ChatMessage> messages) {
            this.messages = messages;
        }
    }

    static class ChatMessage {
        private String role;
        private List<ChatContent> content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public List<ChatContent> getContent() {
            return content;
        }

        public void setContent(List<ChatContent> content) {
            this.content = content;
        }

        // Getters and setters
    }

    static class ChatContent {
        private String type;




        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }



        // Getters and setters
    }


    private static class ImageUrl {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        // Getters and setters
    }
    public static class ImageContent extends ChatContent{


        private Object image_url;

        // Constructor for text content

        // Constructor for image content
        public ImageContent(String type, Object image_url) {
            super();
            this.image_url = image_url;
        }


        public ImageContent() {

        }

        public void setImage_url(Object image_url) {
            this.image_url = image_url;
        }

        public Object getImage_url() {
            return image_url;
        }
    }

    public static class TextContent extends ChatContent {

        private String text;

        public TextContent(String type, String text) {
            super();
            this.text = text;
        }

        public TextContent() {

        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }


    }

    static class ChatResponse {

        private List<Choice> choices;


        public static class Choice {

            private int index;
            private MessageChat message;

            public class MessageChat {
                private String role;
                private String content;

            }
        }
        // Define response structure if needed
    }
}
