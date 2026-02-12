package com.HTT.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HTT.backend.entities.User;


@Repository
public interface UserRepo extends JpaRepository<User, Integer>{

    public Optional<User> findByEmail(String email);
    public Optional<User> findByResetPasswordToken(String resetPasswordToken);
}
