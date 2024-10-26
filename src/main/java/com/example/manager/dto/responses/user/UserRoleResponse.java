package com.example.manager.dto.responses.user;

import lombok.Data;
import java.util.List;


@Data
public class UserRoleResponse {
    private String name;
    private List<RoleResponse> roles;
}
