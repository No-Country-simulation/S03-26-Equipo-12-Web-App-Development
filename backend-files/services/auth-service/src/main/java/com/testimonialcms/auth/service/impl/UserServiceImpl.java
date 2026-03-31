package com.testimonialcms.auth.service.impl;

import com.testimonialcms.auth.domain.dto.request.RegisterRequest;
import com.testimonialcms.auth.domain.dto.response.UserResponse;
import com.testimonialcms.auth.domain.entity.User;
import com.testimonialcms.auth.mapper.UserMapper;
import com.testimonialcms.auth.repository.UserRepository;
import com.testimonialcms.auth.service.UserService;
import com.testimonialcms.shared.dto.response.PagedResponse;
import com.testimonialcms.shared.exception.BusinessException;
import com.testimonialcms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper      userMapper;

    @Override
    @Transactional
    public UserResponse create(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> findAll(Pageable pageable) {
        return PagedResponse.from(
                userRepository.findAll(pageable).map(userMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        user.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void toggleActive(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        user.setActive(!user.getActive());
        userRepository.save(user);
    }
}
