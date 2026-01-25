package com.mcesnik.backend.DTO;

import jakarta.validation.constraints.NotNull;

public record CardAnswerRequest(
        @NotNull Long cardId,
        @NotNull Boolean correct
) {}
