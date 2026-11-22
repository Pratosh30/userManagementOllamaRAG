package com.example.usermanagement.controller;
import com.example.usermanagement.dto.AuthDtos.*;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/users")
public class UserController {
 private final UserService service;public UserController(UserService s){service=s;}
 @GetMapping("/me") public UserResponse me(Authentication a){return service.me(a.getName());}
 @PutMapping("/me") public UserResponse update(Authentication a,@Valid @RequestBody UpdateUserRequest r){return service.update(a.getName(),r);}
}
