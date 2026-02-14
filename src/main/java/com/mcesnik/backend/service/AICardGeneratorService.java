package com.mcesnik.backend.service;

import com.mcesnik.backend.DTO.GeneratedCardDTO;
import com.mcesnik.backend.DTO.GeneratedCardsResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AICardGeneratorService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            You are an expert flashcard creator for educational purposes. Your job is to \\
                      create high-quality flashcards from provided content.
                      
            Rules:
                      1. Each flashcard must have a clear, specific question and a concise, accurate answer.
                      2. Questions should test understanding, not just recall. Use "Why", "How", "Explain", \\
                         "What is the difference between" style questions when appropriate.
                      3. Answers should be concise but complete - typically 1-3 sentences.
                      4. Assign a difficulty from 1 (very easy/basic definition) to 3 (hard).
                      5. Assign a short tag (1-3 words) that categorizes the card's topic.
                      6. Do NOT create duplicate or near-duplicate cards.
                      7. Do NOT create cards about trivial or irrelevant information.
                      8. Questions and answers must be factually accurate based on the provided content.
                      9. If the content is in a specific language, create the flashcards in that same language.
            """;

    public AICardGeneratorService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public GeneratedCardsResponse generateCards(String content){
        if(content == null || content.isBlank()){
            throw new RuntimeException("Content cannot be empty.");
        }

        String userPrompt = buildUserPrompt(content);

        List<GeneratedCardDTO> cards = chatClient.prompt()
                .user(userPrompt)
                .call()
                .entity(new ParameterizedTypeReference<>() {});

        if (cards == null || cards.isEmpty()) {
            throw new RuntimeException("AI failed to generate any flashcards from the provided content");
        }

        List<GeneratedCardDTO> validatedCards = cards.stream()
                .filter(card -> card.question() != null && !card.question().isBlank()
                && card.answer() != null && !card.answer().isBlank())
                .map(card -> new GeneratedCardDTO(
                        card.question().trim(),
                        card.answer().trim(),
                        card.tag() != null ? card.tag().trim() : null,
                        clampDifficulty(card.difficulty())
                ))
                .toList();

        return new GeneratedCardsResponse(validatedCards, validatedCards.size());
    }

    private int clampDifficulty(Integer difficulty){
        if(difficulty == null ) return 1;
        return Math.max(1, Math.min(3, difficulty));
    }

    private String buildUserPrompt(String content){
        return """
                  Generate flashcards from the following content. \
                  Determine the appropriate number of cards based on the content length and complexity. \
                  For short content (under 500 characters), generate 3-5 cards. \
                  For medium content (500-2000 characters), generate 5-15 cards. \
                  For long content (over 2000 characters), generate 10-30 cards.

                  Content:
                  \"\"\"
                  %s
                  \"\"\"
                  """.formatted(content);
    }
}
