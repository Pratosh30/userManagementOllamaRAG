package com.example.usermanagement.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos {
 private AuthDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=255) String email,@NotBlank @Size(min=10,max=72) String password){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record AuthResponse(String accessToken,String tokenType,long expiresIn){}
 public record ForgotPasswordRequest(@NotBlank @Email String email){}
 public record ResetPasswordRequest(@NotBlank String token,@NotBlank @Size(min=10,max=72) String newPassword){}
 public record UserResponse(Long id,String name,String email,String role){}
 public record UpdateUserRequest(@NotBlank @Size(max=100) String name){}
 public record MessageResponse(String message){}
}
