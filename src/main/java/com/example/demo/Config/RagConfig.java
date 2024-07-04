package com.example.demo.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.embedding.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfig {

   private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

   @Value("classpath:/Docs/MarketAi.txt")
    private Resource marketAI;

   @Value("vectorstore.json")
    private String vectorStore;

   @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingClient) {

            SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
            File marketAIFile = getMarketAIFile();
            if(marketAIFile.exists()) {
                log.info("Market AI file is found");

                simpleVectorStore.load(marketAIFile);
            } else {
                log.info("Market AI file not found, creating new one");
                TextReader textReader = new TextReader(marketAI);
                textReader.getCustomMetadata().put("filename", "MarketAi.txt");
                List<Document> documents = textReader.get();
                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> splitDocs = splitter.apply(documents);

                simpleVectorStore.add(splitDocs);
                simpleVectorStore.save(marketAIFile);

                simpleVectorStore.load(marketAI);

            }
            return simpleVectorStore;

    }

    private File getMarketAIFile() {

       Path path = Paths.get("src" , "main" , "resources" , "Docs" );
       String absolutePath = path.toFile().getAbsolutePath() + "/" +vectorStore;
       return new File(absolutePath);

    }

}
