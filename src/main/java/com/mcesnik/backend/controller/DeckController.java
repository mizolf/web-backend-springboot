package com.mcesnik.backend.controller;

import com.mcesnik.backend.DTO.CreateDeckRequest;
import com.mcesnik.backend.DTO.DeckDetailResponse;
import com.mcesnik.backend.DTO.DeckResponse;
import com.mcesnik.backend.DTO.UpdateDeckRequest;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.service.DeckService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {
    private DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping
    public ResponseEntity<DeckResponse> createDeck(
            @RequestBody CreateDeckRequest request,
            @AuthenticationPrincipal User user
    ){
        Deck deck = deckService.createDeck(request, user);
        return ResponseEntity.ok(new DeckResponse(deck));
    }

    @GetMapping
    public ResponseEntity<List<DeckResponse>> getMyDecks(
            @AuthenticationPrincipal User user
    ) {
        List<Deck> decks = deckService.getDecksByOwner(user);
        return ResponseEntity.ok(decks.stream()
                .map(DeckResponse::new)
                .toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<DeckDetailResponse> getDeck(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Deck deck = deckService.getDeckById(id, user);
        return ResponseEntity.ok(new DeckDetailResponse(deck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(
            @PathVariable Long id,
            @RequestBody UpdateDeckRequest request,
            @AuthenticationPrincipal User user) {
        Deck deck = deckService.updateDeck(id, request, user);
        return ResponseEntity.ok(new DeckResponse(deck));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        deckService.deleteDeck(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<List<DeckResponse>> getPublicDecks() {
        List<Deck> decks = deckService.getPublicDecks();
        return ResponseEntity.ok(decks.stream()
                .map(DeckResponse::new)
                .toList());
    }
}
