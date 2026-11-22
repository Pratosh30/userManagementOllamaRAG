package com.example.usermanagement.security;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository users;
 public CustomUserDetailsService(UserRepository users){this.users=users;}
 public UserDetails loadUserByUsername(String email){
  var u=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
  return User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole()).disabled(!u.isEnabled()).build();
 }
}
