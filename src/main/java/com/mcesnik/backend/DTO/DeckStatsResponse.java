package com.mcesnik.backend.DTO;

public record DeckStatsResponse(
        int totalCardsInDeck,
        long cardsStudied,
        long totalCorrect,
        long totalIncorrect,
        double overallAccuracy
) {}
