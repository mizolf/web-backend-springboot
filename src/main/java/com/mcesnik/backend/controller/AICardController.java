package com.mcesnik.backend.controller;

import com.mcesnik.backend.DTO.*;
import com.mcesnik.backend.model.Card;
import com.mcesnik.backend.model.Deck;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.service.AICardGeneratorService;
import com.mcesnik.backend.service.CardService;
import com.mcesnik.backend.service.DeckService;
import com.mcesnik.backend.service.PdfExtractorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/decks/{deckId}/ai-cards")
public class AICardController {
    private final AICardGeneratorService aiCardGeneratorService;
    private final CardService cardService;
    private final DeckService deckService;
    private final PdfExtractorService pdfExtractorService;

    public AICardController(AICardGeneratorService aiCardGeneratorService, CardService cardService, DeckService deckService, PdfExtractorService pdfExtractorService) {
        this.aiCardGeneratorService = aiCardGeneratorService;
        this.cardService = cardService;
        this.deckService = deckService;
        this.pdfExtractorService = pdfExtractorService;
    }

    //HELPER METHODS
    private List<CardResponse> saveCards(Long deckId, List<GeneratedCardDTO> cards, User user) {
        return cards.stream()
                .map(dto -> {
                    CreateCardRequest createReq = new CreateCardRequest(
                            dto.question(), dto.answer(), dto.tag(), dto.difficulty()
                    );
                    Card card = cardService.createCard(deckId, createReq, user);
                    return new CardResponse(card);
                })
                .toList();
    }

    private void validateDeckOwnership(Long deckId, User user) {
        Deck deck = deckService.getDeckById(deckId, user);
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<GeneratedCardsResponse> generateFromText(
            @PathVariable Long deckId,
            @Valid @RequestBody GeneratedCardsRequest request,
            @AuthenticationPrincipal User user
            ){
        validateDeckOwnership(deckId, user);
        GeneratedCardsResponse response = aiCardGeneratorService.generateCards(request.content());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/generate-from-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedCardsResponse> generateFromPdf(
            @PathVariable Long deckId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ){
        validateDeckOwnership(deckId, user);
        String extractedText = pdfExtractorService.extractText(file);
        GeneratedCardsResponse response = aiCardGeneratorService.generateCards(extractedText);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<SaveGeneratedCardsResponse> saveGeneratedCards(
            @PathVariable Long deckId,
            @Valid @RequestBody SaveGeneratedCardsRequest request,
            @AuthenticationPrincipal User user
    ) {
        List<CardResponse> saved = saveCards(deckId, request.cards(), user);
        return ResponseEntity.ok(new SaveGeneratedCardsResponse(saved, saved.size()));
    }

    // Direct: generate and save from text
    @PostMapping("/generate-and-save")
    public ResponseEntity<SaveGeneratedCardsResponse> generateAndSaveFromText(
            @PathVariable Long deckId,
            @Valid @RequestBody GeneratedCardsRequest request,
            @AuthenticationPrincipal User user
    ) {
        GeneratedCardsResponse generated = aiCardGeneratorService.generateCards(request.content());
        List<CardResponse> saved = saveCards(deckId, generated.cards(), user);
        return ResponseEntity.ok(new SaveGeneratedCardsResponse(saved, saved.size()));
    }

    // Direct: generate and save from PDF
    @PostMapping(value = "/generate-and-save-from-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SaveGeneratedCardsResponse> generateAndSaveFromPdf(
            @PathVariable Long deckId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        validateDeckOwnership(deckId, user);
        String extractedText = pdfExtractorService.extractText(file);
        GeneratedCardsResponse generated = aiCardGeneratorService.generateCards(extractedText);
        List<CardResponse> saved = saveCards(deckId, generated.cards(), user);
        return ResponseEntity.ok(new SaveGeneratedCardsResponse(saved, saved.size()));
    }
}
