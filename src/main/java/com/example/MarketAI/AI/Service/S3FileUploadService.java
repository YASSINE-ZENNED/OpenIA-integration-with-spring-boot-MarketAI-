package com.example.MarketAI.AI.Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3FileUploadService {

    @Autowired
    private AmazonS3 amazonS3;
    Dotenv dotenv = Dotenv.load();
    String bucketName = dotenv.get("AWS_BUCKET_NAME");



    public void uploadFile(String key, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), null);
        amazonS3.putObject(putObjectRequest);
    }



    public Resource getAwsFile(String objectKey) throws IOException {

        S3Object object = amazonS3.getObject(bucketName,objectKey);
        var bytes = object.getObjectContent().readAllBytes();
        Resource resource = new ByteArrayResource(bytes);
        return resource;
    }


}
