package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.entity.Photo;
import com.example.demo.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final PhotoRepository photoRepository;

    public S3Service(AmazonS3 amazonS3, PhotoRepository photoRepository) {
        this.amazonS3 = amazonS3;
        this.photoRepository = photoRepository;
    }

    /**
     * userId, postId 포함하여 파일 업로드 및 Photo 엔티티 저장, 반환
     */
    public Photo uploadFile(Long userId, Long postId, MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지는 반드시 첨부해야 합니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        String fileUrl = amazonS3.getUrl(bucket, s3FileName).toString();

        Photo photo = new Photo();
        photo.setPostId(postId);
        photo.setUserId(userId);
        photo.setUrl(fileUrl);

        return photoRepository.save(photo);
    }

    /**
     * 파일만 업로드 후 URL 문자열 반환
     */
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
            amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    /**
     * URL 기반 S3 파일 삭제
     */
    public void deleteFileByUrl(String fileUrl) {
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}