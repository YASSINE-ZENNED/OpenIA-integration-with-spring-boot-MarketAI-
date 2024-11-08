package com.example.MarketAI.AI.Service.serviceImpl;


import com.example.MarketAI.AI.Models.Item;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OpenAiService {
    @Autowired
    private OpenAiImageModel openAiImageModel;

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final ChatClient chatClient;

    @Value("classpath:/Docs/RagPrompt.st")
    private Resource ragPromtTemplate;

    public OpenAiService(VectorStore vectorStore, ChatModel chatModel, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.chatClient = chatClient.defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory())).build();
    }


    public String GenerateImage(String prompt) {
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

    public String SupportChatWithImage(String prompt, byte[] fileContent) {

        SystemMessage systemMessage = new SystemMessage("I'm a customer service agent for a company that sells products online. I need to provide support to customers who have questions about products they're interested in. I need to be able to answer questions about the product's features, price, and availability. I also need to be able to provide information about the company's return policy and shipping options. " +
                "if you dont know the answer to the question just say sorry i dont have that information and if the question is not related to the product just say sorry i cant help you with that question. " +
                "and give them this email to contact us :  Example@gmail.com  and pohne number : 1234567890"

        );
        System.out.println("prompt = " + prompt);

        if (fileContent == null) {

            List<Document> similarDocs = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(1));

            List<String> contentList = similarDocs.stream().map(Document::getContent).toList();

            PromptTemplate promptTemplate = new PromptTemplate(ragPromtTemplate);

            Map<String, Object> promptParams = new HashMap<>();

            promptParams.put("input", prompt);

            promptParams.put("images", null);

            promptParams.put("documents", String.join("\n", contentList));

            Prompt Ragedprompt = promptTemplate.create(promptParams);

            System.out.println("________________________________");

            System.out.println("Ragedprompt = " + Ragedprompt);

            System.out.println("________________________________");

            return chatClient.call(Ragedprompt).getResult().getOutput().getContent();

        } else {

            UserMessage userMessage = new UserMessage(prompt,
                    List.of(new Media(MimeTypeUtils.IMAGE_JPEG, fileContent)));


            return chatClient.prompt(new Prompt(List.of(systemMessage, userMessage))).call().content();

//            return response.getResult().getOutput().getContent();
        }
    }

    public Item describeImageAsUser(String keywords, byte[] fileContent) {


        var listOutputParser = new BeanOutputParser<>(Item.class);
        SystemMessage systemMessage = new SystemMessage("only foucs on the main item in the photo ,I'd like to generate descriptions for items I'm selling on a second-hand marketplace. The items can be either new or used.  Can you create a template that sounds like a real person wrote it, not a big company? i want you to search for prices and get a close estimate for the suitable  price  " +

                "I want the descriptions to be clear and honest about the item's condition add bullet points of key features  be specific if you can  by giving detailed  answer if the you can indetifiy the exact model if you cant tell the item tell to upload better photos of the product or if the item is not ok to sell just say sorry cant help you with this item dont use any name brand  for thr discription use 100 words."
                + "here is the format i want you to use :" + listOutputParser.getFormat()
        );

        UserMessage userMessage = new UserMessage(" if possible use these words { " + keywords + " } if the exact model is present then give more detailed with exact numbers  and technical terms key features ", List.of(new Media(MimeTypeUtils.IMAGE_JPEG, fileContent)));

        var response = chatClient.call(new Prompt(
                List.of(systemMessage, userMessage),
                OpenAiChatOptions.builder().withFunctions(Set.of("getPrices", "getItemDetails")).build()
        ));

        System.out.println("response = " + response.getResult().getOutput().getContent());

////          this code is not implimented yet i need to fix it
//        if (responseBody.startsWith("'''") && responseBody.endsWith("'''")) {
//            responseBody = responseBody.substring(3, responseBody.length() - 3);
//        }


//        System.out.println("response.getResult().getOutput().getContent() = ||" + response.getResult().getOutput().getContent());
        String str = response.getResult().getOutput().getContent().substring(8, response.getResult().getOutput().getContent().length() - 3);
        System.out.println("str = " + str);
//        System.out.println("response = " + response);

        return listOutputParser.parse(str);

    }

    public Item describeImageAsEnterprise(String keywords, byte[] fileContent) {

        var listOutputParser = new BeanOutputParser<>(Item.class);

        SystemMessage systemMessage = new SystemMessage("Your primary function is to generate detailed and accurate descriptions of the main items in images provided by users for marketplace listings ." + "here is the format i want you to use :" + listOutputParser.getFormat());

        UserMessage userMessage = new UserMessage(" use these words { " + keywords + " }",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG, fileContent)));

        var response = chatClient.call(new Prompt(List.of(systemMessage, userMessage)));

        return listOutputParser.parse(response.getResult().getOutput().getContent());

    }

}
