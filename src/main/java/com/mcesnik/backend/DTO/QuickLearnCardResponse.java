package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.Card;

public record QuickLearnCardResponse(
        Long cardId,
        String question,
        String answer
) {
    public QuickLearnCardResponse(Card card) {
        this(card.getId(), card.getQuestion(), card.getAnswer());
    }
}
