package com.todayter.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todayter.domain.comment.dto.CommentResponseDto;
import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.comment.entity.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentResponseDto> getPagedCommentsByBoardAndUser(Long boardId, Long userId) {
        QComment comment = QComment.comment;

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.board.id.eq(boardId))
                .orderBy(comment.createdAt.desc())
                .fetch();

        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getPagedCommentsByBoard(Long boardId) {
        QComment comment = QComment.comment;

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.board.id.eq(boardId))
                .orderBy(comment.createdAt.desc())
                .fetch();

        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

}
