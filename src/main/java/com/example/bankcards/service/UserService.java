package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserDto createUser(CreateUserRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return new UserDto(user.getId(), user.getUsername());
    }

    public void register(RegisterRequest request) {
        String userName = request.userName();
        if (userRepository.findByUsername(userName).isPresent()) throw new RuntimeException("User with such name already exists");
        User user = User.builder()
                .username(userName)
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public void authenticate(LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.userName());
        if (user.isEmpty()) throw new RuntimeException("No such user");

        if (!passwordEncoder.matches(request.password(), user.get().getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
    }
}
