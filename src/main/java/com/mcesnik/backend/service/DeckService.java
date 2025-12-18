package com.mcesnik.backend.service;

import com.mcesnik.backend.DTO.CreateDeckRequest;
import com.mcesnik.backend.DTO.UpdateDeckRequest;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.repository.DeckRepository;

import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;

    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Deck createDeck(CreateDeckRequest request, User owner) {
        Deck deck = new Deck(request.name(), owner);
        deck.setIsPublic(request.isPublic() != null ? request.isPublic() : false);
        return deckRepository.save(deck);
    }

    public List<Deck> getDecksByOwner(User owner) {
        return deckRepository.findByOwner(owner);
    }

    public Deck getDeckById(Long id, User user) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // Provjeri ima li korisnik pristup (vlasnik ili javni deck)
        if (!deck.getOwner().getId().equals(user.getId()) && !deck.getIsPublic()) {
            throw new RuntimeException("Access denied");
        }

        return deck;
    }

    public Deck updateDeck(Long id, UpdateDeckRequest request, User user) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // Samo vlasnik može ažurirati
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (request.name() != null) {
            deck.setName(request.name());
        }
        if (request.isPublic() != null) {
            deck.setIsPublic(request.isPublic());
        }

        return deckRepository.save(deck);
    }

    public void deleteDeck(Long id, User user) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // Samo vlasnik može obrisati
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        deckRepository.delete(deck);
    }

    public List<Deck> getPublicDecks() {
        return deckRepository.findByIsPublicTrue();
    }
}
