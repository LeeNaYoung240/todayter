package com.todayter.global.filter;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BadWordFilter {

    private final Set<String> badWords;

    public BadWordFilter() {
        Dotenv dotenv = Dotenv.load();
        String badWordsStr = dotenv.get("BAD_WORDS", "");
        this.badWords = Arrays.stream(badWordsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public boolean containsBadWord(String content) {
        String lowered = content.toLowerCase();

        return badWords.stream()
                .anyMatch(badWord -> lowered.contains(badWord));
    }
}