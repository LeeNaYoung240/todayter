package com.todayter.domain.board.controller;

import com.todayter.domain.board.dto.BoardRequestDto;
import com.todayter.domain.board.dto.BoardResponseDto;
import com.todayter.domain.board.service.BoardService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping()
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> addBoard(
            @RequestBody BoardRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        BoardResponseDto responseDto = boardService.createBoard(userDetails.getUser(), requestDto);

        return new ResponseEntity<>(new CommonResponseDto<>(201, "게시글 생성에 성공하였습니다. 🎉", responseDto), HttpStatus.CREATED);

    }
}
