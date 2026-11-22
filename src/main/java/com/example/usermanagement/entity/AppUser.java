package com.example.usermanagement.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="app_users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,unique=true,length=255) private String email;
 @Column(name="password_hash",nullable=false) private String passwordHash;
 @Column(nullable=false,length=30) private String role="USER";
 @Column(nullable=false) private boolean enabled=true;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected AppUser(){}
 public AppUser(String name,String email,String passwordHash){this.name=name;this.email=email;this.passwordHash=passwordHash;}
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;}
 public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
 public void setPasswordHash(String p){passwordHash=p;} public String getRole(){return role;} public boolean isEnabled(){return enabled;}
}
