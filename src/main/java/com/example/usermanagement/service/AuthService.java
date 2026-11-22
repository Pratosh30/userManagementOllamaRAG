package com.example.usermanagement.service;
import com.example.usermanagement.dto.AuthDtos.*;
import com.example.usermanagement.entity.AppUser;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthService {
 private final UserRepository users;private final PasswordEncoder encoder;private final AuthenticationManager manager;private final JwtService jwt;
 public AuthService(UserRepository u,PasswordEncoder e,AuthenticationManager m,JwtService j){users=u;encoder=e;manager=m;jwt=j;}
 @Transactional public UserResponse register(RegisterRequest r){
  String email=r.email().trim().toLowerCase();
  if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
  AppUser u=users.saveAndFlush(new AppUser(r.name().trim(),email,encoder.encode(r.password())));
  return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole());
 }
 public AuthResponse login(LoginRequest r){
  String email=r.email().trim().toLowerCase();
  try{manager.authenticate(new UsernamePasswordAuthenticationToken(email,r.password()));}
  catch(AuthenticationException ex){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");}
  AppUser u=users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
  return new AuthResponse(jwt.generate(u),"Bearer",jwt.expiresIn());
 }
}
