package com.todayter.domain.comment.repository;

import com.todayter.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.board.id = :boardId")
    void deleteAllByBoardId(Long boardId);

}
