package com.example.usermanagement.security;
import com.example.usermanagement.entity.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Service
public class JwtService {
 private final SecretKey key; private final long expirationMs;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long exp){
  byte[] bytes; try{bytes=Base64.getDecoder().decode(secret);}catch(IllegalArgumentException e){bytes=secret.getBytes(StandardCharsets.UTF_8);}
  if(bytes.length<32) throw new IllegalArgumentException("JWT_SECRET must provide at least 32 bytes; use a random Base64 secret.");
  key=Keys.hmacShaKeyFor(bytes); expirationMs=exp;
 }
 public String generate(AppUser u){long now=System.currentTimeMillis();return Jwts.builder().subject(u.getEmail()).claim("role",u.getRole()).issuedAt(new Date(now)).expiration(new Date(now+expirationMs)).signWith(key).compact();}
 public Claims claims(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
 public boolean valid(String token){try{claims(token);return true;}catch(JwtException|IllegalArgumentException e){return false;}}
 public long expiresIn(){return expirationMs/1000;}
}
