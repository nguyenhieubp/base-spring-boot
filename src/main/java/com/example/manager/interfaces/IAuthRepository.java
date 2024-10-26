package com.example.manager.interfaces;

import com.example.manager.dto.requests.user.UserRequest;

public interface IAuthRepository {
    public Boolean registerUser (UserRequest userRequest);
}
