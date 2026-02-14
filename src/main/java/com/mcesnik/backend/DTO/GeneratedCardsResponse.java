package com.mcesnik.backend.DTO;

import java.util.List;

public record GeneratedCardsResponse(
        List<GeneratedCardDTO> cards,
        int totalGenerated
) {}
