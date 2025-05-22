package com.mybackend.trade_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.HashMap;
import java.util.Map;
import com.mybackend.trade_backend.dto.LoginRequest;
import com.mybackend.trade_backend.dto.LoginResponse;
import com.mybackend.trade_backend.dto.RegisterRequest;
import com.mybackend.trade_backend.service.UserService;
import com.mybackend.trade_backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);
            
            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (BadCredentialsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "用户名或密码不正确");
            errorResponse.put("error", "invalid_credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "登录失败");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok().build();
    }
}