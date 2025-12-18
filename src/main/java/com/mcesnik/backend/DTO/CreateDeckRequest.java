package com.mcesnik.backend.DTO;

public record CreateDeckRequest (
        String name,
        Boolean isPublic
) { }