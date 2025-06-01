package com.todayter.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchKeywordService {

    private final StringRedisTemplate redisTemplate;
    private static final String POPULAR_KEYWORD_KEY = "popular:keywords";

    public void recordSearchKeyword(String keyword) {
        redisTemplate.opsForZSet().incrementScore(POPULAR_KEYWORD_KEY, keyword, 1);
    }

    public List<String> getTopKeywords(int limit) {
        Set<String> keywordSet = redisTemplate.opsForZSet()
                .reverseRange(POPULAR_KEYWORD_KEY, 0, limit - 1);

        return keywordSet == null ? List.of() : new ArrayList<>(keywordSet);
    }
}