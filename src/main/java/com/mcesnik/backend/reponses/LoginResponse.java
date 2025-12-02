package com.mcesnik.backend.reponses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String message;
    private Long expiresIn;
}
