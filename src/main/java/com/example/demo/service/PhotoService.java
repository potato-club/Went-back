package com.example.demo.service;

import com.example.demo.dto.PhotoDTO;
import com.example.demo.entity.Photo;
import com.example.demo.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public PhotoDTO createPhoto(PhotoDTO photoDTO) {
        Photo photo = new Photo();
        photo.setPostId(photoDTO.getPostId());
        photo.setUrl(photoDTO.getUrl());
        return convertToDTO(photoRepository.save(photo));
    }

    public List<PhotoDTO> getAllPhotos() {
        return photoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PhotoDTO getPhoto(Long id) {
        Photo photo = photoRepository.findById(id).orElse(null);
        return convertToDTO(photo);
    }

    public PhotoDTO updatePhoto(PhotoDTO photoDTO) {
        Photo photo = photoRepository.findById(photoDTO.getPhotoId()).orElse(null);
        if (photo != null) {
            photo.setPostId(photoDTO.getPostId());
            photo.setUrl(photoDTO.getUrl());
            return convertToDTO(photoRepository.save(photo));
        }
        return null;
    }

    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }

    private PhotoDTO convertToDTO(Photo photo) {
        if (photo == null) return null;
        PhotoDTO dto = new PhotoDTO();
        dto.setPhotoId(photo.getPhotoId());
        dto.setPostId(photo.getPostId());
        dto.setUrl(photo.getUrl());
        return dto;
    }
}


