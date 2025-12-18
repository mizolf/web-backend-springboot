package com.mcesnik.backend.repository;

import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByDeck(Deck deck);
}
