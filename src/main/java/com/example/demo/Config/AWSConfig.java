package com.example.demo.Config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    Dotenv dotenv = Dotenv.load();

    // Access the variables
    String secretKey = dotenv.get("AWS_SECRET_KEY");
    String accessKey = dotenv.get("AWS_ACCESS_KEY");
    String region = dotenv.get("AWS_REGION");


    @Bean
    public AmazonS3 amazonS3() {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3Client.builder()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
