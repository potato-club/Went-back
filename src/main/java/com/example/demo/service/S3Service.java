package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // 여러 장 업로드
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> urlList = new ArrayList<>();
        if (files == null) return urlList;
        for (MultipartFile file : files) {
            String url = upload(file);
            urlList.add(url);
        }
        return urlList;
    }

    // 파일 1개 업로드 (S3에만 저장)
    public String upload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지는 반드시 첨부해야 합니다.");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, metadata));
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    // S3 파일 삭제 (DB 무관)
    public void deleteFileByUrl(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        try {
            key = URLDecoder.decode(key, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Key 디코딩 실패", e);
        }
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}