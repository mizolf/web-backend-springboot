package com.mcesnik.backend.DTO;

public record SessionStatsResponse(
        int totalCards,
        int correctCount,
        int incorrectCount,
        double accuracy
) {}
