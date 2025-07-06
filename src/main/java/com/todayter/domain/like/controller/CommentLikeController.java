package com.todayter.domain.like.controller;

import com.todayter.domain.like.dto.CommentLikeResponseDto;
import com.todayter.domain.like.service.CommentLikeService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<CommonResponseDto<CommentLikeResponseDto>> createCommentLike(@PathVariable Long commentId,
                                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentLikeResponseDto response = commentLikeService.createCommentLike(commentId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(201, commentId + "번 댓글 좋아요 성공 🎉", response), HttpStatus.CREATED);
    }

    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<CommonResponseDto<CommentLikeResponseDto>> deleteCommentLike(@PathVariable Long likeId,
                                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentLikeResponseDto response = commentLikeService.deleteCommentLike(likeId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(200, likeId + "번 댓글 좋아요 취소 성공 🎉", response), HttpStatus.OK);
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<CommonResponseDto<CommentLikeResponseDto>> getCommentLike(@PathVariable Long commentId,
                                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentLikeResponseDto response = commentLikeService.getCommentLike(commentId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(200, commentId + "번 댓글 좋아요 조회 성공 🎉", response), HttpStatus.OK);
    }
}