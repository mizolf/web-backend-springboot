package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record DeckResponse(
        Long id,
        String name,
        Boolean isPublic,
        LocalDateTime createdAt,
        Integer cardCount,
        String ownerUsername,
        Double averageDifficulty
) {
    public DeckResponse(Deck deck) {
        this(
                deck.getId(),
                deck.getName(),
                deck.getIsPublic(),
                deck.getCreatedAt(),
                deck.getCards() != null ? deck.getCards().size() : 0,
                deck.getOwner().getName(),
                calculateAverageDifficulty(deck.getCards())
        );
    }

    private static Double calculateAverageDifficulty(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        return cards.stream()
                .map(Card::getDifficulty)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}
