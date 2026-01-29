package com.mcesnik.backend.service;

import com.mcesnik.backend.DTO.CreateDeckRequest;
import com.mcesnik.backend.DTO.UpdateDeckRequest;
import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.repository.DeckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Transactional
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

    public Page<Deck> getDecksByOwner(User owner, Pageable pageable) {
        return deckRepository.findByOwner(owner, pageable);
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

    public Page<Deck> getPublicDecks(Pageable pageable) {
        return deckRepository.findByIsPublicTrue(pageable);
    }

    public Page<Deck> getPublicDecksFiltered(int page, int size, String sortBy,
                                              String sortDir, String sizeFilter, Integer minCards) {
        List<Deck> allDecks = deckRepository.findByIsPublicTrue();

        Stream<Deck> stream = allDecks.stream();

        if (sizeFilter != null && !sizeFilter.isEmpty()) {
            stream = stream.filter(deck -> matchesSizeFilter(deck, sizeFilter));
        }

        if (minCards != null && minCards > 0) {
            stream = stream.filter(deck -> getCardCount(deck) >= minCards);
        }

        List<Deck> filtered = stream.toList();
        List<Deck> sorted = sortDecks(filtered, sortBy, sortDir);

        return toPage(sorted, page, size);
    }

    public Page<Deck> getMyDecksFiltered(User owner, int page, int size,
                                          String sortBy, String sortDir) {
        List<Deck> allDecks = deckRepository.findByOwner(owner);

        List<Deck> sorted;
        if ("averageDifficulty".equals(sortBy)) {
            sorted = sortDecksByAverageDifficulty(allDecks, sortDir);
        } else {
            sorted = sortDecks(allDecks, sortBy, sortDir);
        }

        return toPage(sorted, page, size);
    }

    private boolean matchesSizeFilter(Deck deck, String sizeFilter) {
        int count = getCardCount(deck);
        return switch (sizeFilter.toLowerCase()) {
            case "small" -> count >= 1 && count <= 20;
            case "medium" -> count >= 21 && count <= 60;
            case "large" -> count >= 61;
            case "empty" -> count == 0;
            default -> true;
        };
    }

    private int getCardCount(Deck deck) {
        return deck.getCards() != null ? deck.getCards().size() : 0;
    }

    private Double getAverageDifficulty(Deck deck) {
        List<Card> cards = deck.getCards();
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

    private List<Deck> sortDecks(List<Deck> decks, String sortBy, String sortDir) {
        Comparator<Deck> comparator = switch (sortBy) {
            case "name" -> Comparator.comparing(Deck::getName, String.CASE_INSENSITIVE_ORDER);
            case "cardCount" -> Comparator.comparingInt(this::getCardCount);
            default -> Comparator.comparing(Deck::getCreatedAt);
        };

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return decks.stream().sorted(comparator).toList();
    }

    private List<Deck> sortDecksByAverageDifficulty(List<Deck> decks, String sortDir) {
        Comparator<Deck> comparator;

        if ("asc".equalsIgnoreCase(sortDir)) {
            comparator = Comparator.comparing(
                    this::getAverageDifficulty,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        } else {
            comparator = Comparator.comparing(
                    this::getAverageDifficulty,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
        }

        return decks.stream().sorted(comparator).toList();
    }

    private Page<Deck> toPage(List<Deck> list, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, list.size());
        List<Deck> pageContent = start < list.size() ? list.subList(start, end) : List.of();
        return new PageImpl<>(pageContent, PageRequest.of(page, size), list.size());
    }
}
