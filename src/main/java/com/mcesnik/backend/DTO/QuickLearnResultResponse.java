package com.mcesnik.backend.DTO;

public record QuickLearnResultResponse(
        SessionStatsResponse sessionStats,
        DeckStatsResponse deckStats
) {}
