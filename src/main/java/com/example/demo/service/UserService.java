package com.example.demo.service;

import com.example.demo.dto.UserCreationDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUniqueDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO createUser(UserCreationDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setSocialKey(userDTO.getSocialKey());
        userEntity.setEmail(userDTO.getEmail());
        UserEntity result = userRepository.save(userEntity);
        return result.toUserResponseDTO();
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserEntity::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        return userEntity != null ? userEntity.toUserResponseDTO() : null;
    }

    public UserResponseDTO findUser(UserUniqueDTO userUniqueDTO) {
        List<UserEntity> listResult = userRepository.findBySocialKeyAndEmail(userUniqueDTO.getSocialKey(), userUniqueDTO.getEmail());
        if (listResult.size() != 1) {
            return null;
        }
        UserEntity userEntity = listResult.get(0);
        return userEntity != null ? userEntity.toUserResponseDTO() : null;
    }

    public UserResponseDTO updateUser(UserUpdateDTO userDTO) {
        List<UserEntity> listResult = userRepository.findBySocialKeyAndEmail(userDTO.getSocialKey(), userDTO.getEmail());
        if (listResult.size() != 1) {
            return null;
        }
        UserEntity userEntity = listResult.get(0);
        userEntity.updateByDto(userDTO);
        UserEntity result = userRepository.save(userEntity);
        return result.toUserResponseDTO();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}