package com.todayter.global.filter;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class BadWordRegexFilter {

    private final Pattern badWordPattern;

    public BadWordRegexFilter() {
        Dotenv dotenv = Dotenv.load();
        String regex = dotenv.get("BAD_WORD_REGEX", "");
        this.badWordPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public boolean containsBadWord(String content) {
        return badWordPattern.matcher(content).find();
    }

}