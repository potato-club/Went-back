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

    public String uploadFile(Long userId, Long postId, MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        // 파일 URL 저장
        String fileUrl = amazonS3.getUrl(bucket, s3FileName).toString();

        // DB에 Photo 저장
        Photo photo = new Photo();
        photo.setPostId(postId);
        photo.setUserId(userId);
        photo.setUrl(fileUrl);
        photoRepository.save(photo);

        return fileUrl;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    public void deleteFileByUrl(String fileUrl) {
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}
