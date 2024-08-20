package com.example.MarketAI.AI.Controllers;

import com.example.MarketAI.AI.Models.Item;
import com.example.MarketAI.AI.Service.OpenAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import groovy.util.logging.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiImageModel;
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
        this.chatClient = chatClient.defaultAdvisors( new MessageChatMemoryAdvisor(new InMemoryChatMemory())).build();

    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "prompt", defaultValue = "tell me a joke ") String prompt) {

        String response = chatClient.prompt().user(prompt).call().content();

        return response ;

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

    @PostMapping("/SupportChatWithImage")
    public String SupportChatWithImage(@RequestParam(value = "image" , required = false) MultipartFile imageFile, @RequestParam("prompt")  String keywords ) throws IOException {

        byte[] fileContent = null;

        if (imageFile != null) {
            fileContent = imageFile.getBytes();
        }

        return openAiService.SupportChatWithImage(keywords, fileContent);

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


