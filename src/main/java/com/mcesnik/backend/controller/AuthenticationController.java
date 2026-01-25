package com.mcesnik.backend.controller;


import com.mcesnik.backend.DTO.LoginUserDTO;
import com.mcesnik.backend.DTO.RegisterUserDTO;
import com.mcesnik.backend.DTO.UserResponse;
import com.mcesnik.backend.DTO.VerifiedUserDTO;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.reponses.LoginResponse;
import com.mcesnik.backend.service.AuthenticationService;
import com.mcesnik.backend.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserDTO registerUserDTO){
        User registeredUser = authenticationService.signUp(registerUserDTO);
        return ResponseEntity.ok(new UserResponse(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginUserDTO loginUserDTO,
            HttpServletResponse response,
            CsrfToken csrfToken
    ){
        User authenticatedUser = authenticationService.authenticate(loginUserDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(60*60)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Login successful");
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifiedUserDTO verifiedUserDTO){
        try{
            authenticationService.verifyUser(verifiedUserDTO);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody String email){
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
