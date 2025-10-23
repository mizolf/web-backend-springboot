package com.mcesnik.backend.controller;


import com.mcesnik.backend.DTO.LoginUserDTO;
import com.mcesnik.backend.DTO.RegisterUserDTO;
import com.mcesnik.backend.DTO.VerifiedUserDTO;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.reponses.LoginResponse;
import com.mcesnik.backend.service.AuthenticationService;
import com.mcesnik.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDTO){
        User registeredUser = authenticationService.signUp(registerUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> authenticate(@RequestBody LoginUserDTO loginUserDTO){
        User authenticatedUser = authenticationService.authenticate(loginUserDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(authenticatedUser);
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
}
