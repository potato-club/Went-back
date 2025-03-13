package com.example.demo.service;

import com.example.demo.entity.PostCategory;
import com.example.demo.repository.PostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCategoryService {
    @Autowired
    private PostCategoryRepository postCategoryRepository;

    public PostCategory createPostCategory(PostCategory postCategory) {
        return postCategoryRepository.save(postCategory);
    }

    public List<PostCategory> getAllPostCategories() {
        return postCategoryRepository.findAll();
    }

    public PostCategory getPostCategory(Long id) {
        return postCategoryRepository.findById(id).orElse(null);
    }

    public PostCategory updatePostCategory(PostCategory postCategory) {
        return postCategoryRepository.save(postCategory);
    }

    public void deletePostCategory(Long id) {
        postCategoryRepository.deleteById(id);
    }
}

