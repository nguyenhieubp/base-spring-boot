package com.example.manager.services;

import com.example.manager.dto.requests.user.UserRequest;
import com.example.manager.dto.responses.user.RoleResponse;
import com.example.manager.dto.responses.user.UserResponse;
import com.example.manager.dto.responses.user.UserRoleResponse;
import com.example.manager.models.Roles;
import com.example.manager.models.UserRoles;
import com.example.manager.models.Users;
import com.example.manager.repositories.RoleRepository;
import com.example.manager.repositories.UserRepository;
import com.example.manager.repositories.UserRoleRepository;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private  UserRepository usersRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private List<String> listIndexhas = new ArrayList<>(); // Danh sách lưu các chỉ số đã được random

    public void registerUser(UserRequest userRequest) {
        if (usersRepository.existsByUserName(userRequest.getUserName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        Users user = modelMapper.map(userRequest,Users.class);

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Users newUser = usersRepository.save(user);

        Roles role_user = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        UserRoles userRoles = new UserRoles();
        userRoles.setRole(role_user);
        userRoles.setUser(newUser);

        userRoleRepository.save(userRoles);
    }


    public UserRoleResponse convertToUserRoleResponse(Users user) {
        UserRoleResponse response = new UserRoleResponse();
        response.setName(user.getUserName());

        // Lấy danh sách các vai trò (roles)
        List<RoleResponse> roleResponses = user.getUserRoles().stream()
                .map(userRole -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setRoleName(userRole.getRole().getRoleName());
                    return roleResponse;
                })
                .collect(Collectors.toList());

        response.setRoles(roleResponses);
        return response;
    }

    // Phương thức trả về danh sách UserRoleResponse
    public List<UserRoleResponse> getAllUserRoles() {
        List<Users> users = usersRepository.findAll();

        return users.stream()
                .map(this::convertToUserRoleResponse)
                .collect(Collectors.toList());
    }


    //
    public UserRoleResponse getUserRole(String userId) {
         Users users = usersRepository.findByUserId(userId)
              .orElseThrow(() -> new RuntimeException("Cart not found with id: " + userId));

         UserRoleResponse response = new UserRoleResponse();
         response.setName(users.getUserName());

         List<RoleResponse> roles = users.getUserRoles().stream()
                .map(userRole -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setRoleName(userRole.getRole().getRoleName());
                    return roleResponse;
                })
                .collect(Collectors.toList());

         response.setRoles(roles);

         return response;
    }


    public UserRoleResponse getUserRoleRandom() {
        List<String> allUserIds = usersRepository.findAllUserIds(); // Lấy tất cả userId

        // Nếu không có user nào thì trả về null
        if (allUserIds.isEmpty()) {
            return null;
        }

        // Nếu đã random hết tất cả các user, trả về thông báo kết thúc trò chơi
        if (listIndexhas.size() == allUserIds.size()) {
            return null;
        }

        Random random = new Random();
        String randomUserId;

        // Chọn ngẫu nhiên một userId chưa được chọn
        do {
            randomUserId = allUserIds.get(random.nextInt(allUserIds.size())); // Chọn ngẫu nhiên userId từ danh sách
        } while (listIndexhas.contains(randomUserId)); // Kiểm tra xem userId đã được chọn chưa

        // Thêm userId vào danh sách các userId đã chọn
        listIndexhas.add(randomUserId);

        // Lấy user từ CSDL bằng userId
        Optional<Users> userOptional = usersRepository.findById(randomUserId);
        if (!userOptional.isPresent()) {
            return null; // Xử lý khi user không tồn tại
        }

        Users userRandom = userOptional.get();

        // Tạo response từ user ngẫu nhiên
        UserRoleResponse response = new UserRoleResponse();
        response.setName(userRandom.getUserName());

        // Lấy danh sách vai trò và map qua RoleResponse
        List<RoleResponse> roles = userRandom.getUserRoles().stream()
                .map(userRole -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setRoleName(userRole.getRole().getRoleName());
                    return roleResponse;
                })
                .collect(Collectors.toList());

        response.setRoles(roles);

        return response;
    }


}
