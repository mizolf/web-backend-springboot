package com.mcesnik.backend.repository;

import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.model.UserCardState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardStateRepository extends JpaRepository<UserCardState, Long> {
    Optional<UserCardState> findByUserAndCard(User user, Card card);

    List<UserCardState> findByUserAndCardIn(User user, List<Card> cards);

    @Query("SELECT COALESCE(SUM(ucs.correctCount), 0) FROM UserCardState ucs " +
           "WHERE ucs.user = :user AND ucs.card.deck.id = :deckId")
    Long sumCorrectCountByUserAndDeckId(@Param("user") User user, @Param("deckId") Long deckId);

    @Query("SELECT COALESCE(SUM(ucs.incorrectCount), 0) FROM UserCardState ucs " +
           "WHERE ucs.user = :user AND ucs.card.deck.id = :deckId")
    Long sumIncorrectCountByUserAndDeckId(@Param("user") User user, @Param("deckId") Long deckId);

    @Query("SELECT COUNT(DISTINCT ucs.card) FROM UserCardState ucs " +
           "WHERE ucs.user = :user AND ucs.card.deck.id = :deckId")
    Long countDistinctCardsStudiedByUserAndDeckId(@Param("user") User user, @Param("deckId") Long deckId);
}
