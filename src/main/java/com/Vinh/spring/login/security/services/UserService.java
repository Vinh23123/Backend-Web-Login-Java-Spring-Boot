package com.bezkoder.spring.login.security.services;

import com.bezkoder.spring.login.payload.request.UserDto;

public interface UserService {

    UserDto updateUserById(UserDto userDto, long id);

    UserDto getUserById(long id);
}

