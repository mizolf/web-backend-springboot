package com.mcesnik.backend.DTO;

import java.util.List;

public record QuickLearnSessionResponse(
        Long deckId,
        String deckName,
        int totalCardsInDeck,
        List<QuickLearnCardResponse> cards
) {}
