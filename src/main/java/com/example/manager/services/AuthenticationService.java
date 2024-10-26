package com.example.manager.services;

import com.example.manager.dto.responses.user.UserResponse;
import com.example.manager.models.Users;
import com.example.manager.repositories.RoleRepository;
import com.example.manager.repositories.UserRepository;
import com.example.manager.repositories.UserRoleRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    private  UserRepository usersRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private  UserRoleRepository userRoleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;


    @Value("${jwt.signerKey}")
    private String signerKey;

    private final long EXPIRATION_TIME = 86400000; // 24 giờ
    private final long REFRESH_EXPIRATION_TIME = 2592000000L; // 30 ngày


    public String authenticate(String username, String password) {
        Users user = usersRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid credentials");
        }

        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);
        user.setRefreshToken(refreshToken); // Lưu Refresh Token vào cơ sở dữ liệu
        usersRepository.save(user); // Cập nhật người dùng với Refresh Token

        return accessToken; // Trả về Access Token
    }



    private String generateToken(Users user) {
        // Tạo UserResponse từ đối tượng Users
        UserResponse userDto = new UserResponse();
        userDto.setUserId(user.getUserId());
        userDto.setUserName(user.getUserName());

        // Ánh xạ danh sách vai trò
        Collection<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .collect(Collectors.toList());

        userDto.setRoles(roles); // Đặt vai trò vào UserResponse

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Đặt vai trò vào claims như danh sách
        claims.put("user", userDto); // Đặt UserResponse vào claims

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, signerKey)
                .compact();
    }



    private String generateRefreshToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, signerKey)
                .compact();
    }



    public String refreshAccessToken(String refreshToken) {
        try {
            String username = Jwts.parser()
                    .setSigningKey(signerKey)
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();

            Users user = usersRepository.findByUserName(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid refresh token");
            }

            // Tạo và trả về Access Token mới
            return generateToken(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid refresh token");
        }
    }




    public UserResponse getUserInfoFromJwt(Authentication authentication) {
        // Lấy đối tượng Jwt từ Authentication object
        Jwt jwt = (Jwt) authentication.getCredentials();

        // Lấy các claims từ JWT
        Map<String, Object> claims = jwt.getClaims();

        // Lấy thông tin người dùng từ claim "user"
        Map<String, Object> userClaims = (Map<String, Object>) claims.get("user");
        String userId = (String) userClaims.get("userId");
        String userName = (String) userClaims.get("userName");

        // Trích xuất thông tin vai trò từ claim "roles"
        List<String> roles = (List<String>) claims.get("roles"); // Đọc từ claim "roles" như danh sách

        // Tạo và thiết lập đối tượng UserResponse
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userId);
        userResponse.setUserName(userName);
        userResponse.setRoles(roles); // Sử dụng danh sách roles trực tiếp

        return userResponse;
    }



}
