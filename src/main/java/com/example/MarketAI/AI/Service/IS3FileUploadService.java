package com.example.MarketAI.AI.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IS3FileUploadService {

    static final String AWS_BUCKET_NAME = "AWS_BUCKET_NAME";

    void uploadFile(String key, MultipartFile file) throws IOException;

    Resource getAwsFile(String objectKey) throws IOException;
}
