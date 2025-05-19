package com.todayter.domain.board.service;

import com.todayter.domain.board.dto.BoardRequestDto;
import com.todayter.domain.board.dto.BoardResponseDto;
import com.todayter.domain.board.dto.BoardSummaryDto;
import com.todayter.domain.board.dto.BoardTitleDto;
import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.repository.BoardRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public BoardResponseDto createBoard(UserEntity user, BoardRequestDto requestDto) {

        Board.BoardType type;

        if ("지역별".equals(requestDto.getCategory())) {
            type = Board.BoardType.LOCAL;
        } else if ("분야별".equals(requestDto.getCategory())) {
            type = Board.BoardType.SECTION;
        } else {
            type = Board.BoardType.NORMAL;
        }

        Board board = new Board(user, requestDto, type);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {
        Board board = findById(boardId);

        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsBySection(String sectionType, String sectionName, String sortBy, int page, int size) {
        Sort sort;

        if ("createdAt".equals(sortBy)) {
            sort = Sort.by(Sort.Order.desc("createdAt"));
        } else if ("likeCount".equals(sortBy)) {
            sort = Sort.by(Sort.Order.desc("likeCount"));
        } else {
            throw new CustomException(ErrorCode.SORT_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Board> boards;

        if ("지역별".equals(sectionType)) {
            if (sectionName == null || sectionName.isEmpty() || "ALL".equalsIgnoreCase(sectionName)) {
                boards = boardRepository.findAllByType(Board.BoardType.LOCAL, pageable);
            } else {
                boards = boardRepository.findAllByTypeAndRegion(Board.BoardType.LOCAL, sectionName, pageable);
            }
        } else if ("분야별".equals(sectionType)) {
            if (sectionName == null || sectionName.isEmpty() || "ALL".equalsIgnoreCase(sectionName)) {
                boards = boardRepository.findAllByType(Board.BoardType.SECTION, pageable);
            } else {
                boards = boardRepository.findAllByTypeAndCategory(Board.BoardType.SECTION, sectionName, pageable);
            }
        } else if ("ALL".equalsIgnoreCase(sectionType)) {
            boards = boardRepository.findAll(pageable);
        } else {
            throw new CustomException(ErrorCode.SECTION_TYPE_NOT_FOUND);
        }

        return boards.map(BoardResponseDto::new);
    }

    @Transactional
    public BoardResponseDto setPick(Long boardId, boolean pick, UserEntity user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!user.hasRole(UserRoleEnum.Authority.ADMIN)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        board.setPick(pick);

        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getPickedBoards(String sortBy, int page, int size) {
        Sort sort;

        if ("createdAt".equals(sortBy)) {
            sort = Sort.by(Sort.Order.desc("createdAt"));
        } else if ("likeCount".equals(sortBy)) {
            sort = Sort.by(Sort.Order.desc("likeCount"));
        } else {
            throw new CustomException(ErrorCode.SORT_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Board> boards = boardRepository.findAllByPickTrue(pageable);

        return boards.map(BoardResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<BoardTitleDto> getBoardTitles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        return boardRepository.findAll(pageable).map(board -> new BoardTitleDto(board.getTitle()));
    }

    public Page<BoardSummaryDto> getBoardSummaries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Board> boards = boardRepository.findAll(pageable);

        return boards.map(board -> new BoardSummaryDto(
                board.getTitle(),
                board.getContents(),
                board.getCategory(),
                board.getUser().getNickname(),
                board.getCreatedAt()
        ));
    }

    @Transactional(readOnly = true)
    public Board findById(Long boardId) {

        return boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );
    }

}
