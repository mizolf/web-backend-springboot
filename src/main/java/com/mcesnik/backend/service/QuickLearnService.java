package com.mcesnik.backend.service;

import com.mcesnik.backend.DTO.*;
import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.model.UserCardState;
import com.mcesnik.backend.repository.CardRepository;
import com.mcesnik.backend.repository.UserCardStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuickLearnService {
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final UserCardStateRepository userCardStateRepository;

    public QuickLearnService(DeckService deckService, CardRepository cardRepository,
                             UserCardStateRepository userCardStateRepository) {
        this.deckService = deckService;
        this.cardRepository = cardRepository;
        this.userCardStateRepository = userCardStateRepository;
    }

    public QuickLearnSessionResponse startSession(Long deckId, User user) {
        Deck deck = deckService.getDeckById(deckId, user);
        List<Card> allCards = cardRepository.findByDeck(deck);

        if (allCards.isEmpty()) {
            throw new RuntimeException("Deck has no cards");
        }

        int count = (int) Math.ceil(allCards.size() / 2.0);

        List<Card> shuffled = new ArrayList<>(allCards);
        Collections.shuffle(shuffled);
        List<Card> selectedCards = shuffled.subList(0, count);

        List<QuickLearnCardResponse> cardResponses = selectedCards.stream()
                .map(QuickLearnCardResponse::new)
                .toList();

        return new QuickLearnSessionResponse(
                deck.getId(),
                deck.getName(),
                allCards.size(),
                cardResponses
        );
    }

    @Transactional
    public QuickLearnResultResponse submitQuickLearn(Long deckId, SubmitQuickLearnRequest request, User user) {
        Deck deck = deckService.getDeckById(deckId, user);
        List<Card> deckCards = cardRepository.findByDeck(deck);

        Map<Long, Card> deckCardMap = deckCards.stream()
                .collect(Collectors.toMap(Card::getId, Function.identity()));

        List<Card> answeredCards = new ArrayList<>();
        for (CardAnswerRequest answer : request.answers()) {
            Card card = deckCardMap.get(answer.cardId());
            if (card == null) {
                throw new RuntimeException("Card " + answer.cardId() + " does not belong to this deck");
            }
            answeredCards.add(card);
        }

        Map<Long, UserCardState> existingStates = userCardStateRepository
                .findByUserAndCardIn(user, answeredCards)
                .stream()
                .collect(Collectors.toMap(ucs -> ucs.getCard().getId(), Function.identity()));

        int sessionCorrect = 0;
        int sessionIncorrect = 0;

        for (CardAnswerRequest answer : request.answers()) {
            Card card = deckCardMap.get(answer.cardId());
            UserCardState state = existingStates.get(answer.cardId());

            if (state == null) {
                state = new UserCardState(card, user);
            }

            if (answer.correct()) {
                state.setCorrectCount(state.getCorrectCount() + 1);
                sessionCorrect++;
            } else {
                state.setIncorrectCount(state.getIncorrectCount() + 1);
                sessionIncorrect++;
            }

            state.setLastReviewedAt(LocalDateTime.now());
            userCardStateRepository.save(state);
        }

        int totalSessionCards = request.answers().size();
        double sessionAccuracy = totalSessionCards > 0
                ? (sessionCorrect * 100.0) / totalSessionCards
                : 0.0;

        SessionStatsResponse sessionStats = new SessionStatsResponse(
                totalSessionCards,
                sessionCorrect,
                sessionIncorrect,
                sessionAccuracy
        );

        long totalCorrect = userCardStateRepository.sumCorrectCountByUserAndDeckId(user, deckId);
        long totalIncorrect = userCardStateRepository.sumIncorrectCountByUserAndDeckId(user, deckId);
        long cardsStudied = userCardStateRepository.countDistinctCardsStudiedByUserAndDeckId(user, deckId);

        long totalAttempts = totalCorrect + totalIncorrect;
        double overallAccuracy = totalAttempts > 0
                ? (totalCorrect * 100.0) / totalAttempts
                : 0.0;

        DeckStatsResponse deckStats = new DeckStatsResponse(
                deckCards.size(),
                cardsStudied,
                totalCorrect,
                totalIncorrect,
                overallAccuracy
        );

        return new QuickLearnResultResponse(sessionStats, deckStats);
    }
}
