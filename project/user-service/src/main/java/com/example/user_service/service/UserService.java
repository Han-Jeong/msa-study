package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    Iterable<UserEntity> getUsersByAll();
}
