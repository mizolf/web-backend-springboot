package com.mcesnik.backend.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveGeneratedCardsRequest(
        @NotEmpty @Valid List<GeneratedCardDTO> cards
) {}
