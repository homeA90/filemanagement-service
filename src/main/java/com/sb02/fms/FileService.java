package com.sb02.fms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    public FileResponseDto uploadFile(MultipartFile file, String description) {
        log.info("Uploading file");

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();

        String s3Key = UUID.randomUUID() + "-" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), size));
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Error uploading file to S3", e);
        }

        String s3Url = baseUrl + "/" + s3Key;

        FileEntity fileEntity = new FileEntity(
            fileName,
            description,
            contentType,
            s3Url,
            s3Key,
            size
        );

        fileRepository.save(fileEntity);

        log.info("File uploaded successfully: {}", s3Key);

        return new FileResponseDto(
                fileEntity.getId(),
                fileEntity.getFileName(),
                fileEntity.getDescription(),
                fileEntity.getContentType(),
                fileEntity.getS3Url(),
                fileEntity.getSize()
        );
    }

    public FileResponseDto getFileById(Long id) {
        log.info("Fetching file with ID: {}", id);

        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return new FileResponseDto(
                fileEntity.getId(),
                fileEntity.getFileName(),
                fileEntity.getDescription(),
                fileEntity.getContentType(),
                fileEntity.getS3Url(),
                fileEntity.getSize()
        );
    }
}
