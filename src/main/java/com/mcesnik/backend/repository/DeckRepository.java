package com.mcesnik.backend.repository;

import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;                                                                                                                                                                                              import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findByOwner(User owner);

    List<Deck> findByIsPublicTrue();
}
