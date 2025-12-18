package com.mcesnik.backend.service;

import com.mcesnik.backend.DTO.CreateCardRequest;
import com.mcesnik.backend.DTO.UpdateCardRequest;
import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final DeckService deckService;

    public CardService(CardRepository cardRepository, DeckService deckService) {
        this.cardRepository = cardRepository;
        this.deckService = deckService;
    }

    public List<Card> getCardsByDeck(Long deckId, User user) {
        Deck deck = deckService.getDeckById(deckId, user);
        return cardRepository.findByDeck(deck);
    }

    public Card getCard(Long deckId, Long cardId, User user) {
        Deck deck = deckService.getDeckById(deckId, user);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // Provjeri pripada li kartica ovom decku
        if (!card.getDeck().getId().equals(deck.getId())) {
            throw new RuntimeException("Card does not belong to this deck");
        }

        return card;
    }

    public Card createCard(Long deckId, CreateCardRequest request, User user) {
        Deck deck = deckService.getDeckById(deckId, user);

        // Samo vlasnik može dodavati kartice
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        Card card = new Card(deck, request.question(), request.answer());
        if (request.tag() != null) {
            card.setTag(request.tag());
        }
        if (request.difficulty() != null) {
            card.setDifficulty(request.difficulty());
        }

        return cardRepository.save(card);
    }

    public Card updateCard(Long deckId, Long cardId, UpdateCardRequest request, User user) {
        Deck deck = deckService.getDeckById(deckId, user);

        // Samo vlasnik može ažurirati
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getDeck().getId().equals(deck.getId())) {
            throw new RuntimeException("Card does not belong to this deck");
        }

        if (request.question() != null) {
            card.setQuestion(request.question());
        }
        if (request.answer() != null) {
            card.setAnswer(request.answer());
        }
        if (request.tag() != null) {
            card.setTag(request.tag());
        }
        if (request.difficulty() != null) {
            card.setDifficulty(request.difficulty());
        }

        return cardRepository.save(card);
    }

    public void deleteCard(Long deckId, Long cardId, User user) {
        Deck deck = deckService.getDeckById(deckId, user);

        // Samo vlasnik može brisati
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getDeck().getId().equals(deck.getId())) {
            throw new RuntimeException("Card does not belong to this deck");
        }

        cardRepository.delete(card);
    }
}
