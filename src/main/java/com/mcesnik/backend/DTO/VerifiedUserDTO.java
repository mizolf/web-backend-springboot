package com.mcesnik.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifiedUserDTO {
    private String email;
    private String verificationCode;
}
