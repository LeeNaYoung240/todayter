package com.todayter.domain.board.service;

import com.todayter.domain.board.dto.*;
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

import java.util.Collections;
import java.util.List;

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

    @Transactional
    public BoardResponseDto getBoard(Long boardId) {
        Board board = findById(boardId);

        boardRepository.updateHits(boardId);
        boardRepository.updateHourHits(boardId);
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

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardUpdateRequestDto boardUpdateRequestDto, UserEntity user) {
        Board board = findById(boardId);
        validateUserMatch(board, user);
        board.update(boardUpdateRequestDto);

        return new BoardResponseDto(board);
    }

    @Transactional
    public void deleteBoard(Long boardId, UserEntity user) {
        Board board = findById(boardId);
        validateUserMatch(board, user);
        boardRepository.delete(board);
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
                board.getSubTitle(),
                board.getContents(),
                board.getCategory(),
                board.getUser().getNickname(),
                board.getCreatedAt()
        ));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsByAdmin(UserEntity user, int page, int size) {
        if (!user.hasRole(UserRoleEnum.Authority.ADMIN)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Board> boards = boardRepository.findAllByUser(user, pageable);

        return boards.map(BoardResponseDto::new);
    }

    @Transactional
    public BoardResponseDto approveBoard(Long boardId, UserEntity adminUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!adminUser.isAdmin()) {
            throw new CustomException(ErrorCode.ADMIN_ACCESS);
        }

        board.setApproved(true);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional
    public BoardResponseDto disapproveBoard(Long boardId, UserEntity adminUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!adminUser.isAdmin()) {
            throw new CustomException(ErrorCode.ADMIN_ACCESS);
        }

        if (!board.isApproved()) {
            throw new CustomException(ErrorCode.BOARD_ALREADY_DISAPPROVED);
        }

        board.setApproved(false);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getApprovedBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        return boardRepository.findAllByApprovedTrue(pageable)
                .map(BoardResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getUnapprovedBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        return boardRepository.findAllByApprovedFalse(pageable)
                .map(BoardResponseDto::new);
    }

    public Page<BoardResponseDto> searchBoards(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return boardRepository.findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword, pageRequest)
                .map(BoardResponseDto::new);
    }

    public long getTotalBoardCnt() {
        return boardRepository.count();
    }

    public long getApprovedCnt() {
        return boardRepository.countByApprovedTrue();
    }

    public long getUnapprovedCnt() {
        return boardRepository.countByApprovedFalse();
    }

    @Transactional(readOnly = true)
    public Board findById(Long boardId) {

        return boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );
    }

    private void validateUserMatch(Board board, UserEntity user) {
        if (!board.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_NOT_MATCH_WITH_BOARD);
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getRanking() {

        return boardRepository.getBoardIdRanking()
                .orElse(Collections.emptyList());
    }

    public void deleteAllHourHits() {
        boardRepository.deleteAllHourHits();
    }

}
