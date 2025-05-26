package com.todayter.domain.like.service;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.repository.BoardRepository;
import com.todayter.domain.board.service.BoardService;
import com.todayter.domain.like.dto.LikeResponseDto;
import com.todayter.domain.like.entity.Like;
import com.todayter.domain.like.repository.LikeRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public LikeResponseDto createBoardLike(Long boardId, UserEntity user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (likeRepository.existsByUserAndBoard(user, board)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        Like like = new Like(user, board);
        likeRepository.save(like);

        board.addLikeCnt();

        return new LikeResponseDto(like, true);
    }

    @Transactional
    public LikeResponseDto deleteBoardLike(Long likeId, UserEntity user) {
        Like foundLike = likeRepository.findById(likeId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_EXIST));

        Board foundBoard = foundLike.getBoard();

        if (!user.getId().equals(foundLike.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_OTHERS_LIKE);
        }

        likeRepository.delete(foundLike);
        foundBoard.minusLikeCount();

        return new LikeResponseDto(null, false);
    }

    public LikeResponseDto getLike(Long boardId, UserEntity user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Like like = likeRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        return new LikeResponseDto(like, false);
    }

}
