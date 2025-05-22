package com.mybackend.trade_backend.config;

import com.mybackend.trade_backend.entity.Permission;
import com.mybackend.trade_backend.entity.Role;
import com.mybackend.trade_backend.entity.User;
import com.mybackend.trade_backend.repository.PermissionRepository;
import com.mybackend.trade_backend.repository.RoleRepository;
import com.mybackend.trade_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository, 
            RoleRepository roleRepository, 
            PermissionRepository permissionRepository, 
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 创建权限
        Permission readData = createPermissionIfNotFound("READ_DATA");
        Permission writeData = createPermissionIfNotFound("WRITE_DATA");
        Permission readAdvancedData = createPermissionIfNotFound("READ_ADVANCED_DATA");
        Permission manageUsers = createPermissionIfNotFound("MANAGE_USERS");

        // 创建角色
        Role adminRole = createRoleIfNotFound("ADMIN", 
                new HashSet<>(Arrays.asList(readData, writeData, readAdvancedData, manageUsers)));
        
        Role userRole = createRoleIfNotFound("USER",
                new HashSet<>(Arrays.asList(readData)));
        
        Role managerRole = createRoleIfNotFound("MANAGER",
                new HashSet<>(Arrays.asList(readData, writeData, readAdvancedData)));

        // 创建用户
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@example.com");
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(admin);
        }
        
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setEmail("user@example.com");
            user.setEnabled(true);
            user.setRoles(new HashSet<>(Arrays.asList(userRole)));
            userRepository.save(user);
        }
        
        if (userRepository.findByUsername("manager").isEmpty()) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager"));
            manager.setEmail("manager@example.com");
            manager.setEnabled(true);
            manager.setRoles(new HashSet<>(Arrays.asList(managerRole)));
            userRepository.save(manager);
        }
    }

    private Permission createPermissionIfNotFound(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    permission.setDescription("Permission to " + name.toLowerCase().replace('_', ' '));
                    return permissionRepository.save(permission);
                });
    }

    private Role createRoleIfNotFound(String name, Set<Permission> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(name + " role");
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                });
    }
}