package com.todayter.domain.board.service;

import com.todayter.domain.board.dto.BoardRequestDto;
import com.todayter.domain.board.dto.BoardResponseDto;
import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.repository.BoardRepository;
import com.todayter.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardResponseDto createBoard(UserEntity user, BoardRequestDto requestDto) {
        Board board = new Board(user ,requestDto, Board.BoardType.NORMAL);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }
}
