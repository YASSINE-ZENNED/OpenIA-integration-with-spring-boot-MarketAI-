package com.example.MarketAI.AI.Controllers;

import com.example.MarketAI.AI.Service.serviceImpl.S3FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/S3")
public class AWSController {

    @Autowired
    private S3FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileUploadService.uploadFile(file.getOriginalFilename(), file);
            return "File uploaded successfully!";
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("key") String key) {
        Resource resource = null;
        try {
            resource = fileUploadService.getAwsFile(key);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                    .body(resource);

//            return fileUploadService.getAwsFile(key).toString();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
