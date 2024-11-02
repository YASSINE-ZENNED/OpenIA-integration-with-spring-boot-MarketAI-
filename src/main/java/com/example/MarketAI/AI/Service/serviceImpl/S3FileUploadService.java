package com.example.MarketAI.AI.Service.serviceImpl;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.MarketAI.AI.Service.IS3FileUploadService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3FileUploadService implements IS3FileUploadService {

    @Autowired
    private AmazonS3 amazonS3;
    final Dotenv dotenv = Dotenv.load();
    final String bucketName = dotenv.get(AWS_BUCKET_NAME);

    public String uploadFile(String key, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), null);
        amazonS3.putObject(putObjectRequest);
        String fileUrl = amazonS3.getUrl(bucketName, key).toString();
        return fileUrl;
    }

    public Resource getAwsFile(String objectKey) throws IOException {
        final S3Object object = amazonS3.getObject(bucketName, objectKey);
        var bytes = object.getObjectContent().readAllBytes();
        return new ByteArrayResource(bytes);
    }
}
