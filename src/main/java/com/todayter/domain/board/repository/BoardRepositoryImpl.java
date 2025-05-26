package com.todayter.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayter.domain.board.entity.QBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRespositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<List<Long>> getBoardIdRanking() {
        QBoard board = QBoard.board;

        List<Long> boardIds = jpaQueryFactory.select(board.id)
                .from(board)
                .orderBy(board.hourHits.desc(), board.id.asc())
                .limit(5)
                .fetch();

        return Optional.ofNullable(boardIds);
    }

    @Override
    @Transactional
    public void deleteAllHourHits() {
        QBoard board = QBoard.board;

        jpaQueryFactory.update(board)
                .set(board.hourHits, 0L)
                .execute();
    }

}
