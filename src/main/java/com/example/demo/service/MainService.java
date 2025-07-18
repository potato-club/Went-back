package com.example.demo.service;

import com.example.demo.dto.response.MainContentResponseDTO;

public interface MainService {
    MainContentResponseDTO getMainContent(Long userId);
}
