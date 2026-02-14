package com.mcesnik.backend.DTO;

import java.util.List;

public record SaveGeneratedCardsResponse(
        List<CardResponse> savedCards,
        int totalSaved
) {}
