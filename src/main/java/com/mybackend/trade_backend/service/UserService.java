package com.mybackend.trade_backend.service;

import com.mybackend.trade_backend.dto.RegisterRequest;
import com.mybackend.trade_backend.entity.Role;
import com.mybackend.trade_backend.entity.User;
import com.mybackend.trade_backend.repository.RoleRepository;
import com.mybackend.trade_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        // 默认为普通用户
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }
}