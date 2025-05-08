package com.birthae.be.utils;

import com.birthae.be.common.exception.BizRuntimeException;
import com.birthae.be.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Util {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BizRuntimeException("파일이 비어 있거나 null입니다.");
        }

        String filename;
        try {
            filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        } catch (NullPointerException e) {
            throw new BizRuntimeException("파일 이름이 null입니다.");
        }

        String key = prefix + "/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new BizRuntimeException("S3에 파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                key);
    }
}