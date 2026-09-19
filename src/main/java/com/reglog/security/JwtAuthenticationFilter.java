package com.reglog.security;

import com.reglog.entity.JwtAuthToken;
import com.reglog.entity.User;
import com.reglog.repository.JwtTokenRepository;
import com.reglog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            JwtTokenRepository jwtTokenRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromCookie(request);

        if (token != null
                && jwtService.isTokenValid(token)
                && isTokenValidInDatabase(token)) {

            String userId = jwtService.extractUserId(token);

            User user = userRepository
                    .findById(userId)
                    .orElse(null);

            if (user != null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getUserId(),
                                null,
                                Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {

            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private boolean isTokenValidInDatabase(
            String token) {

        Optional<JwtAuthToken> tokenOptional =
                jwtTokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        JwtAuthToken jwtAuthToken =
                tokenOptional.get();

        LocalDateTime now = LocalDateTime.now();

        return jwtAuthToken
                .getExpiresAt()
                .isAfter(now);
    }
}