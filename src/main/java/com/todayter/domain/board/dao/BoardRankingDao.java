package com.todayter.domain.board.dao;

import com.todayter.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardRankingDao {

    private final StringRedisTemplate stringRedisTemplate;
    private final BoardService boardService;

    public void saveRanking(List<Long> boardIds) {
        for (int i = 0; i < boardIds.size(); i++) {
            stringRedisTemplate.opsForValue()
                    .set(String.valueOf(i + 1), String.valueOf(boardIds.get(i)));
        }
        boardService.deleteAllHourHits();
    }

    public List<Long> getRanking() {
        List<Long> ranking = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String key = String.valueOf(i);
            String value = stringRedisTemplate.opsForValue().get(key);

            if (value != null) {
                try {
                    ranking.add(Long.parseLong(value));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format for key " + key);
                }
            }
        }

        return ranking;
    }

}