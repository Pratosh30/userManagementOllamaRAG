package com.example.usermanagement.service;
import com.example.usermanagement.dto.AuthDtos.*;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service public class UserService {
 private final UserRepository users;public UserService(UserRepository u){users=u;}
 public UserResponse me(String email){var u=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole());}
 @Transactional public UserResponse update(String email,UpdateUserRequest r){var u=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));u.setName(r.name().trim());return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole());}
}
