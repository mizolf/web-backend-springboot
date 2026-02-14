package com.mcesnik.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeneratedCardsRequest(
        @NotBlank @Size(min = 10, max = 50000) String content
) {}
