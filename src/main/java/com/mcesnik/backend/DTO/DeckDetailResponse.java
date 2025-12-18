package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.Deck;

import java.time.LocalDateTime;
import java.util.List;

public record DeckDetailResponse(

    Long id,
    String name,
    Boolean isPublic,
    LocalDateTime createdAt,
    String ownerUsername,
    Integer cardCount,
    List<CardResponse> cards
) {
    public DeckDetailResponse(Deck deck) {
        this(
                deck.getId(),
                deck.getName(),
                deck.getIsPublic(),
                deck.getCreatedAt(),
                deck.getOwner().getUsername(),
                deck.getCards().size(),
                deck.getCards().stream()
                        .map(CardResponse::new)
                        .toList()
        );
    }
}