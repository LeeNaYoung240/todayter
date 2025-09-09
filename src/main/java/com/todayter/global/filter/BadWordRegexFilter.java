package com.todayter.global.filter;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class BadWordRegexFilter {

    private final Pattern badWordPattern;

    public BadWordRegexFilter() {
        Dotenv dotenv;

        try {
            // 먼저 로컬 경로 시도
            dotenv = Dotenv.configure()
                    .directory("D:/Project/todayter")
                    .load();
        } catch (Exception e) {
            // 실패하면 Docker 경로 시도
            dotenv = Dotenv.configure()
                    .directory("/app")  // Docker .env 경로
                    .load();
        }

        String regex = dotenv.get("BAD_WORD_REGEX", "");
        Pattern tempPattern;
        try {
            tempPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            tempPattern = Pattern.compile("");
            System.err.println("BAD_WORD_REGEX 패턴이 잘못되었습니다: " + e.getMessage());
        }
        this.badWordPattern = tempPattern;
    }

    public boolean containsBadWord(String content) {
        return badWordPattern.matcher(content).find();
    }
}
