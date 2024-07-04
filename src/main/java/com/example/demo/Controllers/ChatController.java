package com.example.demo.Controllers;

import com.example.demo.Models.Item;
import com.example.demo.Service.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChatController {

    @Autowired
    private OpenAiImageModel openAiImageModel;
    @Autowired
    private OpenAiService openAiService;

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final ChatClient chatClient;

    @Value("classpath:/Docs/RagPrompt.st")
    private Resource ragPromtTemplate;

    public ChatController(VectorStore vectorStore, ChatModel chatModel , ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.chatClient = chatClient.build();

    }

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestParam(value = "prompt", defaultValue = "tell me a joke ") String prompt) {
        return Map.of("generation", chatModel.call(prompt));
    }

    @GetMapping("/Assistantchat")
    public String assistantchat(@RequestParam(value = "prompt", defaultValue = "tell me about this app and why should i use it ") String prompt) {
        List<Document>  similarDocs = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(1));
        List<String> contentList = similarDocs.stream().map(Document::getContent).toList();
        PromptTemplate promptTemplate = new PromptTemplate(ragPromtTemplate);
        Map<String,Object> promptParams =new HashMap<>();
        promptParams.put("input", prompt);
        promptParams.put("documents", String.join("\n", contentList));
        Prompt Ragedprompt = promptTemplate.create(promptParams);


        return chatClient.call(Ragedprompt).getResult().getOutput().getContent();
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



