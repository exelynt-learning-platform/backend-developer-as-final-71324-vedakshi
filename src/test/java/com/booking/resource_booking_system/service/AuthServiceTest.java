package com.booking.resource_booking_system.service;

import com.booking.resource_booking_system.dto.LoginRequest;
import com.booking.resource_booking_system.dto.LoginResponse;
import com.booking.resource_booking_system.entity.User;
import com.booking.resource_booking_system.repository.UserRepository;
import com.booking.resource_booking_system.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequest loginRequest;


    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        loginRequest = new LoginRequest();

        loginRequest.setUsername("user");
        loginRequest.setPassword("password");
    }


    
    @Test
    void login_shouldLoginSuccessfully() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "user",
                "USER"
        )).thenReturn("test-jwt-token");

        LoginResponse response =
                authService.login(loginRequest);

        assertNotNull(response);

        assertEquals(
                "test-jwt-token",
                response.getToken()
        );

        assertEquals(
                "user",
                response.getUsername()
        );

        assertEquals(
                "USER",
                response.getRole()
        );

        verify(userRepository, times(1))
                .findByUsername("user");

        verify(passwordEncoder, times(1))
                .matches(
                        "password",
                        "encodedPassword"
                );

        verify(jwtService, times(1))
                .generateToken(
                        "user",
                        "USER"
                );
    }



    @Test
    void login_shouldFailWhenUsernameDoesNotExist() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .matches(
                        anyString(),
                        anyString()
                );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        anyString()
                );
    }


   

    @Test
    void login_shouldFailWhenPasswordIsIncorrect() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"
        )).thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        anyString()
                );
    }
}

