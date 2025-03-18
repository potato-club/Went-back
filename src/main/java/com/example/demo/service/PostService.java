package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public PostDTO createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId());
        post.setContent(postDTO.getContent());
        post.setCategoryId(postDTO.getCategoryId());
        return convertToDTO(postRepository.save(post));
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO getPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        return convertToDTO(post);
    }

    public PostDTO updatePost(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId()).orElse(null);
        if (post != null) {
            post.setUserId(postDTO.getUserId());
            post.setContent(postDTO.getContent());
            post.setCategoryId(postDTO.getCategoryId());
            return convertToDTO(postRepository.save(post));
        }
        return null;
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private PostDTO convertToDTO(Post post) {
        if (post == null) return null;
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setCategoryId(post.getCategoryId());
        return dto;
    }
}


