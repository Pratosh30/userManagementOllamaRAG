package com.example.usermanagement.controller;
import com.example.usermanagement.dto.AuthDtos.*;
import com.example.usermanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService auth;private final PasswordResetService reset;
 public AuthController(AuthService a,PasswordResetService r){auth=a;reset=r;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public UserResponse register(@Valid @RequestBody RegisterRequest r){return auth.register(r);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}
 @PostMapping("/forgot-password") public MessageResponse forgot(@Valid @RequestBody ForgotPasswordRequest r){reset.request(r.email());return new MessageResponse("If an account exists for that email, password-reset instructions have been generated.");}
 @PostMapping("/reset-password") public MessageResponse reset(@Valid @RequestBody ResetPasswordRequest r){reset.reset(r.token(),r.newPassword());return new MessageResponse("Password reset successfully.");}
}
