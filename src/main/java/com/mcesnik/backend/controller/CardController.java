package com.mcesnik.backend.controller;

import com.mcesnik.backend.DTO.CardResponse;
import com.mcesnik.backend.DTO.CreateCardRequest;
import com.mcesnik.backend.DTO.UpdateCardRequest;
import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks/{deckId}/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards(
            @PathVariable Long deckId,
            @AuthenticationPrincipal User user
    ) {
        List<Card> cards = cardService.getCardsByDeck(deckId, user);
        return ResponseEntity.ok(cards.stream()
                .map(CardResponse::new)
                .toList());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @AuthenticationPrincipal User user
    ) {
        Card card = cardService.getCard(deckId, cardId, user);
        return ResponseEntity.ok(new CardResponse(card));
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @PathVariable Long deckId,
            @Valid @RequestBody CreateCardRequest request,
            @AuthenticationPrincipal User user
    ) {
        Card card = cardService.createCard(deckId, request, user);
        return ResponseEntity.ok(new CardResponse(card));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardRequest request,
            @AuthenticationPrincipal User user
    ) {
        Card card = cardService.updateCard(deckId, cardId, request, user);
        return ResponseEntity.ok(new CardResponse(card));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @AuthenticationPrincipal User user
    ) {
        cardService.deleteCard(deckId, cardId, user);
        return ResponseEntity.noContent().build();
    }
}
