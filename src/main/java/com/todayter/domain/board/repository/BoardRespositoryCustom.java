package com.todayter.domain.board.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRespositoryCustom {

    void deleteAllHourHits();

    Optional<List<Long>> getBoardIdRanking();

}
