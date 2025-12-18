package com.mcesnik.backend.DTO;

public record UpdateCardRequest (
        String question,
        String answer,
        String tag,
        Integer difficulty
) {
}
