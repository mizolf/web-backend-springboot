package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.Deck;

import java.time.LocalDateTime;

public record DeckResponse (
    Long id,
    String name,
    Boolean isPublic,
    LocalDateTime createdAt,
    Integer cardCount
)
{
    public DeckResponse(Deck deck) {
        this(
                deck.getId(),
                deck.getName(),
                deck.getIsPublic(),
                deck.getCreatedAt(),
                deck.getCards() != null ? deck.getCards().size() : 0
        );
    }
}
