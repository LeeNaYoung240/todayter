package com.todayter.domain.board.entity;

import com.todayter.domain.board.dao.BoardRankingDao;
import com.todayter.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardDBChecker {

    private final DataSource dataSource;
    private final BoardService boardService;
    private final BoardRankingDao rankingDao;

    @Scheduled(fixedRate = 3600000)
    public void checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                System.out.println("===== BoardDBChecker.checkDatabase() 호출됨 =====");

                List<Long> boardIds = boardService.getRanking();

                if (boardIds != null && !boardIds.isEmpty()) {
                    System.out.println(boardIds);
                    rankingDao.saveRanking(boardIds);
                } else {
                    System.out.println("No schedule IDs found.");
                }
            } else {
                System.out.println("Database connection is not valid.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
        }
    }
}