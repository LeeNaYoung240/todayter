package com.todayter.domain.board.repository;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.user.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRespositoryCustom {

    long count();

    long countByApprovedTrue();

    long countByApprovedFalse();

    Page<Board> findAllByTypeAndApprovedTrue(Board.BoardType type, Pageable pageable);

    Page<Board> findAllByTypeAndRegionAndApprovedTrue(Board.BoardType type, String region, Pageable pageable);

    Page<Board> findAllByTypeAndSectionAndApprovedTrue(Board.BoardType type, String section, Pageable pageable);


    Page<Board> findAllByPickTrueAndApprovedTrue(Pageable pageable);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findAllByUser(UserEntity user, Pageable pageable);

    @Modifying
    @Query("UPDATE Board s SET s.hits = s.hits + 1 WHERE s.id = :boardId")
    int updateHits(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE Board s SET s.hourHits = s.hourHits + 1 WHERE s.id = :boardId")
    int updateHourHits(@Param("boardId") Long boardId);

    Page<Board> findByApprovedTrueAndTitleContainingIgnoreCaseOrApprovedTrueAndContentsContainingIgnoreCase(String title, String contents, Pageable pageable);

    Page<Board> findAllByApprovedTrue(Pageable pageable);

    Page<Board> findAllByApprovedFalse(Pageable pageable);

    @Query("""
                SELECT b FROM Board b
                JOIN FETCH b.user u
                LEFT JOIN FETCH u.followers
                WHERE b.id = :boardId
            """)
    Optional<Board> findByIdWithUserAndFollowers(@Param("boardId") Long boardId);

    Page<Board> findAllByUser_Id(Long userId, Pageable pageable);

    Page<Board> findAllByUser_IdAndApprovedTrue(Long userId, Pageable pageable);

}
