package com.reglog.repository;

import com.reglog.entity.JwtAuthToken;
import com.reglog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtAuthToken, Long> {

    Optional<JwtAuthToken> findByToken(String token);

    Optional<JwtAuthToken> findByTokenAndUser(String token, User user);

    void deleteByToken(String token);

    void deleteByUser(User user);
}