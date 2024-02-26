package com.Vinh.spring.login.security.services.impl;

import com.Vinh.spring.login.exception.ResourceNotFoundException;
import com.Vinh.spring.login.payload.request.UserDto;
import com.Vinh.spring.login.security.services.UserService;
import com.Vinh.spring.login.security.services.impl.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Vinh.spring.login.models.User;
import com.Vinh.spring.login.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService,UserService {
  final UserRepository userRepository;
  private ModelMapper mapper;
  public UserDetailsServiceImpl(UserRepository userRepository, ModelMapper mapper) {
    this.userRepository = userRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }


  public List<UserDto> getAllUsers(){
    List<User> users = userRepository.findAll();
    List<UserDto>  userListDto  = new ArrayList<>();
    for (User user : users) {
      userListDto.add(mapToDto(user));

    }
    return userListDto;
  }

  @Override
  public UserDto updateUserById(UserDto userDto, long id){
      User user =  userRepository.findById(id)
                  .orElseThrow(()-> new ResourceNotFoundException("User","id", id));

      user.setFirstname(userDto.getFirstname());
      user.setLastname(userDto.getLastname());
      user.setEmail(userDto.getEmail());

      User updatedUser = userRepository.save(user);
      return mapToDto(updatedUser);
  }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user){
      UserDto userDto = mapper.map(user, UserDto.class);
      return userDto;
  }

  private User mapToUEntity(UserDto userDto){
    User user = mapper.map(userDto, User.class);
    return user;
  }
}
