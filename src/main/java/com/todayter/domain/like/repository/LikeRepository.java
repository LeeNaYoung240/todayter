package com.todayter.domain.like.repository;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.like.entity.Like;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndBoard(UserEntity user, Board foundBoard);

    Optional<Like> findById(Long likeId);

    Optional<Like> findByBoardAndUser(Board board, UserEntity user);

    Long countByBoardId(Long boardId);

}
