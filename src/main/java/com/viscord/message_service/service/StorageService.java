package com.viscord.message_service.service;

import com.viscord.message_service.enums.StorageCategory;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

    public String uploadFile(MultipartFile file, StorageCategory type, String entityId) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = String.format("%s/%s/%s", type.getPath(), entityId, UUID.randomUUID() + "." + extension);

        try {
             s3Template.upload(this.bucketName, key, file.getInputStream());

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file input stream", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to AWS S3", e);
        }
    }
}
