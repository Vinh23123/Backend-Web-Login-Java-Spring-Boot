package com.Vinh.spring.login.repository;

import java.util.Optional;

import com.Vinh.spring.login.payload.request.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Vinh.spring.login.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}

