package com.example.Job.Application.Tracking.System.service;

import com.example.Job.Application.Tracking.System.dto.UserRequestDTO;
import com.example.Job.Application.Tracking.System.dto.UserResponseDTO;
import com.example.Job.Application.Tracking.System.entity.User;
import com.example.Job.Application.Tracking.System.exception.DuplicateEmailException;
import com.example.Job.Application.Tracking.System.exception.ResourceNotFoundException;
import com.example.Job.Application.Tracking.System.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO registerUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("A user with this email already exists: " + dto.getEmail());
        }

        User user = new User(dto.getName(), dto.getEmail(), dto.getRole());
        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = findUserOrThrow(id);
        return toResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}