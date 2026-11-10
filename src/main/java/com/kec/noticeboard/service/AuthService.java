package com.kec.noticeboard.service;

import com.kec.noticeboard.config.JwtTokenProvider;
import com.kec.noticeboard.dto.LoginRequest;
import com.kec.noticeboard.dto.LoginResponse;
import com.kec.noticeboard.entity.User;
import com.kec.noticeboard.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public LoginResponse authenticateAdmin(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = tokenProvider.generateToken(user.getUsername(), user.getRole().name(), user.getFullName());

            return new LoginResponse(
                    true,
                    "Login successful",
                    jwt,
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().name()
            );
        } catch (BadCredentialsException e) {
            return LoginResponse.failure("Invalid username or password");
        } catch (Exception e) {
            return LoginResponse.failure("Authentication failed: " + e.getMessage());
        }
    }
}
