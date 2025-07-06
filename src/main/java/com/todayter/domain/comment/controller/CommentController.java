package com.todayter.domain.comment.controller;

import com.todayter.domain.comment.dto.CommentRequestDto;
import com.todayter.domain.comment.dto.CommentResponseDto;
import com.todayter.domain.comment.service.CommentFilterService;
import com.todayter.domain.comment.service.CommentService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class CommentController {

    private final CommentService commentService;
    private final CommentFilterService commentFilterService;

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommonResponseDto> createComment(@PathVariable Long boardId,
                                                           @Valid @RequestBody CommentRequestDto commentRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentFilterService.validateCommentContent(commentRequestDto.getContent());
        CommentResponseDto commentResponseDto = commentService.createComment(commentRequestDto, boardId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.CREATED.value(), "댓글 생성에 성공하였습니다. 🎉", commentResponseDto), HttpStatus.CREATED);
    }

    @GetMapping("{boardId}/comments")
    public ResponseEntity<CommonResponseDto<List<CommentResponseDto>>> getBoardComments(@PathVariable Long boardId) {
        List<CommentResponseDto> commentResponseDtos = commentService.getBoardComments(boardId);

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "댓글 조회에 성공하였습니다. 🎉", commentResponseDtos), HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<CommonResponseDto<List<CommentResponseDto>>> getAllComments() {
        List<CommentResponseDto> commentResponseDtos = commentService.getAllComments();

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "전체 댓글 조회에 성공하였습니다. 🎉", commentResponseDtos), HttpStatus.OK);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponseDto> updateComment(@PathVariable Long commentId,
                                                           @Valid @RequestBody CommentRequestDto commentRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentFilterService.validateCommentContent(commentRequestDto.getContent());
        CommentResponseDto commentResponseDto = commentService.updateComment(commentRequestDto, commentId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "댓글 수정에 성공하였습니다. 🎉", commentResponseDto), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponseDto> deleteComment(@PathVariable Long commentId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "댓글 삭제에 성공하였습니다. 🎉", null), HttpStatus.OK);

    }

    @DeleteMapping("/admin/comments/{commentId}")
    public ResponseEntity<CommonResponseDto> deleteCommentByAdmin(@PathVariable Long commentId,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteCommentByAdmin(commentId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "관리자가 댓글 삭제에 성공하였습니다. 🎉", null), HttpStatus.OK);
    }

    @GetMapping("/comment-cnt")
    public ResponseEntity<CommonResponseDto<Long>> getCommentCnt() {
        long totalComments = commentService.getTotalCommentCnt();

        return ResponseEntity.ok(new CommonResponseDto<>(200, "전체 댓글 수 조회에 성공하였습니다. 🎉", totalComments));
    }

}
