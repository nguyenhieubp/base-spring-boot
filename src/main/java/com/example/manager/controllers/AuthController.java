package com.example.manager.controllers;

import com.example.manager.dto.requests.user.UserRequest;
import com.example.manager.dto.responses.auth.AuthResponse;
import com.example.manager.dto.responses.common.ApiResponse;
import com.example.manager.dto.responses.user.UserResponse;
import com.example.manager.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Value("${jwt.signerKey}")
    private String signerKey;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody UserRequest user) {
        String accessToken = authenticationService.authenticate(user.getUserName(), user.getPassword());
        AuthResponse authResponse = new AuthResponse(accessToken);
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(201,"success",authResponse);
        return ResponseEntity.ok(apiResponse); // Trả về Access Token
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        // Sử dụng AuthenticationService để xử lý logic trích xuất thông tin từ JWT
        UserResponse userResponse = authenticationService.getUserInfoFromJwt(authentication);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(200,"success",userResponse);
        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/admin")
    public ResponseEntity<?> checkAdmin() {
        return ResponseEntity.ok("ADMIN");
    }

    @GetMapping("/user")
    public ResponseEntity<?> checkUser() {
        return ResponseEntity.ok("USER");
    }


    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // loại bỏ "Bearer "
        Claims claims = Jwts.parser()
                .setSigningKey(signerKey)
                .parseClaimsJws(token)
                .getBody();
        return ResponseEntity.ok(claims);
    }




}
