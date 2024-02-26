package com.Vinh.spring.login.payload.request;


import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Set<String>roles;
    private String firstname;
    private String lastname;
}
