package com.todayter.domain.like.controller;


import com.todayter.domain.like.dto.LikeResponseDto;
import com.todayter.domain.like.service.LikeService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}/likes")
    public ResponseEntity<CommonResponseDto<LikeResponseDto>> createBoardLike(@PathVariable Long boardId,
                                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LikeResponseDto likeResponseDto = likeService.createBoardLike(boardId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(201, boardId + "번 일정에 대한 좋아요 등록을 성공하였습니다. 🎉", likeResponseDto), HttpStatus.CREATED);
    }

    @DeleteMapping("likes/{likeId}")
    public ResponseEntity<CommonResponseDto<LikeResponseDto>> deleteBoardLike(@PathVariable Long likeId,
                                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LikeResponseDto likeResponseDto = likeService.deleteBoardLike(likeId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(200, likeId + "번 좋아요 취소에 성공하였습니다. 🎉", likeResponseDto), HttpStatus.OK);
    }

    @GetMapping("/{boardId}/likes")
    public ResponseEntity<CommonResponseDto<LikeResponseDto>> getLike(@PathVariable Long boardId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LikeResponseDto likeResponseDto = likeService.getLike(boardId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(200, boardId + "번 좋아요 조회에 성공하였습니다. 🎉", likeResponseDto), HttpStatus.OK);
    }

    @GetMapping("/{boardId}/likes/count")
    public ResponseEntity<CommonResponseDto<Long>> getLikeCount(@PathVariable Long boardId) {
        Long count = likeService.getLikeCount(boardId);

        return ResponseEntity.ok(new CommonResponseDto<>(200, "좋아요 수 조회 성공", count));
    }

}
