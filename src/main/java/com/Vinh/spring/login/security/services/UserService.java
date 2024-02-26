package com.Vinh.spring.login.security.services;

import com.Vinh.spring.login.payload.request.UserDto;

public interface UserService {

    UserDto updateUserById(UserDto userDto, long id);

    UserDto getUserById(long id);
}

