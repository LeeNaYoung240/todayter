package com.todayter.domain.board.service;

import com.todayter.domain.board.dto.*;
import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.repository.BoardRepository;
import com.todayter.domain.file.entity.File;
import com.todayter.domain.file.service.FileService;
import com.todayter.domain.follow.repository.FollowRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final FollowRepository followRepository;

    @Transactional
    public BoardResponseDto createBoard(UserEntity user, BoardRequestDto requestDto, List<MultipartFile> multipartFiles) {
        Board.BoardType type = switch (requestDto.getCategory()) {
            case "지역별" -> Board.BoardType.LOCAL;
            case "분야별" -> Board.BoardType.SECTION;
            default -> Board.BoardType.NORMAL;
        };

        Board board = new Board(user, requestDto, type);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<File> files = fileService.uploadFile(multipartFiles);
            board.getFiles().addAll(files);
        }

        boardRepository.save(board);

        Board loadedBoard = boardRepository.findByIdWithUserAndFollowers(board.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        int followerCnt = followRepository.countByFollowing(loadedBoard.getUser());
        return new BoardResponseDto(loadedBoard, followerCnt);
    }

    @Transactional
    public BoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findByIdWithUserAndFollowers(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        boardRepository.updateHits(boardId);
        boardRepository.updateHourHits(boardId);

        int followerCnt = followRepository.countByFollowing(board.getUser());
        return new BoardResponseDto(board, followerCnt);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardUpdateRequestDto dto,
                                        List<MultipartFile> newImages, UserEntity user) {
        Board board = boardRepository.findByIdWithUserAndFollowers(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        validateUserMatch(board, user);
        board.update(dto);

        if (newImages != null && !newImages.isEmpty()) {
            List<File> newFiles = fileService.uploadFile(newImages);
            board.getFiles().addAll(newFiles);
        }

        int followerCnt = followRepository.countByFollowing(board.getUser());
        return new BoardResponseDto(board, followerCnt);
    }

    @Transactional
    public BoardResponseDto approveBoard(Long boardId, UserEntity adminUser) {
        Board board = boardRepository.findByIdWithUserAndFollowers(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!adminUser.isAdmin()) throw new CustomException(ErrorCode.ADMIN_ACCESS);
        board.setApproved(true);
        boardRepository.save(board);

        int followerCnt = followRepository.countByFollowing(board.getUser());
        return new BoardResponseDto(board, followerCnt);
    }

    @Transactional
    public BoardResponseDto disapproveBoard(Long boardId, UserEntity adminUser) {
        Board board = boardRepository.findByIdWithUserAndFollowers(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!adminUser.isAdmin()) throw new CustomException(ErrorCode.ADMIN_ACCESS);
        if (!board.isApproved()) throw new CustomException(ErrorCode.BOARD_ALREADY_DISAPPROVED);

        board.setApproved(false);
        boardRepository.save(board);

        int followerCnt = followRepository.countByFollowing(board.getUser());
        return new BoardResponseDto(board, followerCnt);
    }

    @Transactional
    public BoardResponseDto setPick(Long boardId, boolean pick, UserEntity user) {
        Board board = boardRepository.findByIdWithUserAndFollowers(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!user.hasRole(UserRoleEnum.Authority.ADMIN)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        board.setPick(pick);

        int followerCnt = followRepository.countByFollowing(board.getUser());
        return new BoardResponseDto(board, followerCnt);
    }

    @Transactional
    public void deleteBoard(Long boardId, UserEntity user) {
        Board board = findById(boardId); // 이건 사용 안 되면 제거해도 됨
        validateUserMatch(board, user);

        for (File file : board.getFiles()) {
            fileService.deleteFile(file.getFileUrl());
        }

        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardTitleDto> getBoardTitles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        return boardRepository.findAll(pageable)
                .map(BoardTitleDto::new);
    }


    @Transactional(readOnly = true)
    public Page<BoardSummaryDto> getBoardSummaries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return boardRepository.findAll(pageable).map(board ->
                new BoardSummaryDto(
                        board.getTitle(),
                        board.getSubTitle(),
                        board.getContents(),
                        board.getCategory(),
                        board.getUser().getNickname(),
                        board.getCreatedAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsByAdmin(UserEntity user, int page, int size) {
        if (!user.hasRole(UserRoleEnum.Authority.ADMIN)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return boardRepository.findAllByUser(user, pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getPickedBoards(String sortBy, int page, int size) {
        Sort sort = sortBy.equals("likeCount")
                ? Sort.by(Sort.Order.desc("likeCount"))
                : Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        return boardRepository.findAllByPickTrue(pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getApprovedBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return boardRepository.findAllByApprovedTrue(pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getUnapprovedBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return boardRepository.findAllByApprovedFalse(pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardsBySection(String sectionType, String sectionName, String sortBy, int page, int size) {
        Sort sort = "likeCount".equals(sortBy)
                ? Sort.by(Sort.Order.desc("likeCount"))
                : Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Board> boards;

        if ("지역별".equals(sectionType)) {
            boards = sectionName == null || sectionName.equals("ALL")
                    ? boardRepository.findAllByType(Board.BoardType.LOCAL, pageable)
                    : boardRepository.findAllByTypeAndRegion(Board.BoardType.LOCAL, sectionName, pageable);
        } else if ("분야별".equals(sectionType)) {
            boards = sectionName == null || sectionName.equals("ALL")
                    ? boardRepository.findAllByType(Board.BoardType.SECTION, pageable)
                    : boardRepository.findAllByTypeAndSection(Board.BoardType.SECTION, sectionName, pageable);
        } else if ("ALL".equalsIgnoreCase(sectionType)) {
            boards = boardRepository.findAll(pageable);
        } else {
            throw new CustomException(ErrorCode.SECTION_TYPE_NOT_FOUND);
        }

        return boards.map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getPopularBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("hits")));
        return boardRepository.findAllByApprovedTrue(pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> searchBoards(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return boardRepository.findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword, pageable)
                .map(board -> new BoardResponseDto(board, followRepository.countByFollowing(board.getUser())));
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

    private void validateUserMatch(Board board, UserEntity user) {
        if (!board.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_NOT_MATCH_WITH_BOARD);
        }
    }

    public void deleteAllHourHits() {
        boardRepository.deleteAllHourHits();
    }

    // 미사용 시 제거 가능
    @Transactional(readOnly = true)
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Long> getRanking() {
        return boardRepository.getBoardIdRanking().orElse(Collections.emptyList());
    }
}
