package com.mcesnik.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(
    @NotBlank String question,
    @NotBlank String answer,
    String tag,
    Integer difficulty
){ }
