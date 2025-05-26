package com.todayter.domain.board.repository;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findAllByType(Board.BoardType type, Pageable pageable);

    Page<Board> findAllByTypeAndRegion(Board.BoardType type, String region, Pageable pageable);

    Page<Board> findAllByTypeAndCategory(Board.BoardType type, String category, Pageable pageable);

    Page<Board> findAllByPickTrue(Pageable pageable);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findAllByUser(UserEntity user, Pageable pageable);


}
