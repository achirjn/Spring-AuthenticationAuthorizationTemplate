package com.HTT.backend.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.HTT.backend.entities.User;

public interface UserService extends UserDetailsService{

    public User saveUser(User user);
    public User findByResetPasswordToken(String token);
}
