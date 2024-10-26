package com.example.manager.controllers;

import com.example.manager.dto.requests.user.UserRequest;
import com.example.manager.dto.responses.common.ApiResponse;
import com.example.manager.dto.responses.user.UserRoleResponse;
import com.example.manager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${jwt.signerKey}")
    private String signerKey;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRequest user) {
        userService.registerUser(user); // Phương thức để đăng ký người dùng
        ApiResponse<String> apiResponse = new ApiResponse<>(201,"success","User registered successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user_and_role")
    public ResponseEntity<ApiResponse<List<UserRoleResponse>>> getAllUserAndRole(){
        ApiResponse<List<UserRoleResponse>> apiResponse = new ApiResponse<>(200,"success",userService.getAllUserRoles());
        return ResponseEntity.ok(apiResponse);
    };


    @GetMapping("/user_and_role/{userId}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> getAllUserAndRoleByUserId(@PathVariable String userId){
        ApiResponse<UserRoleResponse> apiResponse = new ApiResponse<>(200,"success",userService.getUserRole(userId));
        return ResponseEntity.ok(apiResponse);
    };


    @GetMapping("/user_and_role/random")
    public ResponseEntity<ApiResponse<UserRoleResponse>> getAllUserAndRoleRandom(){
        UserRoleResponse user = userService.getUserRoleRandom();
        if(user == null){
            ApiResponse<UserRoleResponse> apiResponse = new ApiResponse<>(200,"success",null);
            return ResponseEntity.ok(apiResponse);
        }
        ApiResponse<UserRoleResponse> apiResponse = new ApiResponse<>(200,"success",user);
        return ResponseEntity.ok(apiResponse);
    };
}
