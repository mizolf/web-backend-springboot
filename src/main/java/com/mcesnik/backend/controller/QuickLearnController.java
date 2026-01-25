package com.mcesnik.backend.controller;

import com.mcesnik.backend.DTO.QuickLearnResultResponse;
import com.mcesnik.backend.DTO.QuickLearnSessionResponse;
import com.mcesnik.backend.DTO.SubmitQuickLearnRequest;
import com.mcesnik.backend.model.User;
import com.mcesnik.backend.service.QuickLearnService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks/{deckId}/learn")
public class QuickLearnController {
    private final QuickLearnService quickLearnService;

    public QuickLearnController(QuickLearnService quickLearnService) {
        this.quickLearnService = quickLearnService;
    }

    @PostMapping("/start")
    public ResponseEntity<QuickLearnSessionResponse> startSession(
            @PathVariable Long deckId,
            @AuthenticationPrincipal User user
    ) {
        QuickLearnSessionResponse response = quickLearnService.startSession(deckId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuickLearnResultResponse> submitQuickLearn(
            @PathVariable Long deckId,
            @Valid @RequestBody SubmitQuickLearnRequest request,
            @AuthenticationPrincipal User user
    ) {
        QuickLearnResultResponse response = quickLearnService.submitQuickLearn(deckId, request, user);
        return ResponseEntity.ok(response);
    }
}
