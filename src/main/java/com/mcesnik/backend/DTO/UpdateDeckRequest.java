package com.mcesnik.backend.DTO;

public record UpdateDeckRequest (
        String name,
        Boolean isPublic
)  { }
