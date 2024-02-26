package com.Vinh.spring.login.controllers;


import com.Vinh.spring.login.exception.ResourceNotFoundException;
import com.Vinh.spring.login.models.User;
import com.Vinh.spring.login.payload.request.UserDto;
import com.Vinh.spring.login.repository.UserRepository;
import com.Vinh.spring.login.security.services.UserService;
import com.Vinh.spring.login.security.services.impl.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RequestMapping("/api/v1")
public class UserController {

    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepository;

    private UserService userService;
    public UserController(UserDetailsServiceImpl userDetailsService, UserRepository userRepository, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    //admin
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>>  getAllUsers () {
        List<UserDto> users = userDetailsService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
//        List<User> users = userRepository.findAll();
//        return users;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id){
        return  ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteUserById(@PathVariable("id") Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not exist with", "id",id));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("DELETED", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

//    @DeleteMapping("/students/{id}")
//    public ResponseEntity<Map<String, Boolean>> deleteStudentById(@PathVariable("id") Long id){
//        User user = userRepository.findById(id)
//                .orElseThrow(()-> new ResourceNotFoundException("Student not exist with", "id", id));
//
//        userRepository.delete(user);
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("DElETED", Boolean.TRUE);
//        return ResponseEntity.ok(response);
//    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserById (@PathVariable("id") Long id,  @RequestBody UserDto userDtoDetails){
        UserDto userResponse = userService.updateUserById(userDtoDetails,id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }




}
