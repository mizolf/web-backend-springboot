package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.Card;

public record CardResponse(
        Long id,
        String question,
        String answer,
        String tag,
        Integer difficulty
) {
    public CardResponse(Card card) {
        this(
                card.getId(),
                card.getQuestion(),
                card.getAnswer(),
                card.getTag(),
                card.getDifficulty()
        );
    }
}
