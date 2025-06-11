package com.todayter.domain.board.controller;

import com.todayter.domain.board.dao.BoardRankingDao;
import com.todayter.domain.board.dto.*;
import com.todayter.domain.board.service.BoardService;
import com.todayter.domain.board.service.SearchKeywordService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;
    private final BoardRankingDao boardRankingDao;
    private final SearchKeywordService searchKeywordService;

    @PostMapping()
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> addBoard(@RequestBody BoardRequestDto requestDto,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.createBoard(userDetails.getUser(), requestDto);

        return new ResponseEntity<>(new CommonResponseDto<>(201, "게시글 생성에 성공하였습니다. 🎉", responseDto), HttpStatus.CREATED);

    }

    @GetMapping("/{boardId}")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> getBoard(@PathVariable Long boardId) {
        BoardResponseDto responseDto = boardService.getBoard(boardId);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "일정 단건 조회에 성공하였습니다. 🎉", responseDto));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> updateBoard(@PathVariable Long boardId,
                                                                           @Valid @RequestBody BoardUpdateRequestDto boardUpdateRequestDto,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.updateBoard(boardId, boardUpdateRequestDto, userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 수정에 성공하였습니다. 🎉", responseDto));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> deleteBoard(@PathVariable Long boardId,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUser());

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 삭제에 성공하였습니다. 🎉", null), HttpStatus.OK);
    }

    @GetMapping("/pick")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> getPickBoard(@RequestParam(value = "page") int page,
                                                                                  @RequestParam(value = "sortBy") String sortBy) {

        Page<BoardResponseDto> boards = boardService.getPickedBoards(sortBy, page - 1, 5);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "Pick 게시글을 " + sortBy + " 순으로 조회에 성공하였습니다. 🎉", boards)
        );
    }

    @GetMapping("/section")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> getSectionBoards(@RequestParam(value = "page") int page,
                                                                                      @RequestParam(value = "sortBy") String sortBy,
                                                                                      @RequestParam(value = "sectionType") String sectionType,
                                                                                      @RequestParam(value = "sectionName", required = false) String sectionName
    ) {
        Page<BoardResponseDto> boards = boardService.getBoardsBySection(sectionType, sectionName, sortBy, page - 1, 10);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), (sectionName == null || sectionName.isEmpty() ? "전체" : sectionName) + " 섹션 게시글을 " + sortBy + " 순으로 조회에 성공하였습니다. 🎉", boards));
    }

    @PatchMapping("/{boardId}/pick")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> setPickBoard(@PathVariable Long boardId,
                                                                            @RequestParam boolean pick,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        BoardResponseDto responseDto = boardService.setPick(boardId, pick, userDetails.getUser());

        String message = pick ? "게시글을 PICK으로 지정하였습니다. 🎉" : "게시글 PICK 지정을 해제하였습니다.";

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), message, responseDto));
    }

    @GetMapping("/titles")
    public ResponseEntity<CommonResponseDto<Page<BoardTitleDto>>> getBoardTitles(@RequestParam(value = "page") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<BoardTitleDto> titles = boardService.getBoardTitles(page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 제목 목록 조회에 성공하였습니다. 🎉", titles));
    }

    @GetMapping("/summaries")
    public ResponseEntity<CommonResponseDto<Page<BoardSummaryDto>>> getBoardSummaries(@RequestParam(value = "page") int page,
                                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<BoardSummaryDto> summaries = boardService.getBoardSummaries(page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "요약형 게시글 목록 조회에 성공하였습니다. 🎉", summaries));
    }

    @GetMapping("/admin")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> getMyBoards(@RequestParam(value = "page") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<BoardResponseDto> boards = boardService.getBoardsByAdmin(userDetails.getUser(), page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "관리자 본인 작성 게시글 조회에 성공하였습니다. 🎉", boards));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> searchBoards(@RequestParam("keyword") String keyword,
                                                                                  @RequestParam(value = "page") int page,
                                                                                  @RequestParam(value = "size", defaultValue = "10") int size) {

        searchKeywordService.recordSearchKeyword(keyword);

        Page<BoardResponseDto> results = boardService.searchBoards(keyword, page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 검색에 성공하였습니다. 🎉", results));
    }

    @PatchMapping("/{boardId}/approve")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> approveBoard(@PathVariable Long boardId,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.approveBoard(boardId, userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 승인이 완료되었습니다. ✅", responseDto));
    }

    @GetMapping("/approved")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> getApprovedBoards(@RequestParam(value = "page") int page,
                                                                                       @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<BoardResponseDto> boards = boardService.getApprovedBoards(page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "승인된 기사 목록 조회에 성공하였습니다. 🎉", boards));
    }

    @PatchMapping("/{boardId}/disapprove")
    public ResponseEntity<CommonResponseDto<BoardResponseDto>> disapproveBoard(@PathVariable Long boardId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.disapproveBoard(boardId, userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "게시글 승인 취소가 완료되었습니다. ❌", responseDto));
    }

    @GetMapping("/unapproved")
    public ResponseEntity<CommonResponseDto<Page<BoardResponseDto>>> getUnapprovedBoards(@RequestParam(value = "page") int page,
                                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<BoardResponseDto> boards = boardService.getUnapprovedBoards(page - 1, size);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "미승인 기사 목록 조회에 성공하였습니다. 🎉", boards));
    }

    @GetMapping("/ranking")
    public ResponseEntity<CommonResponseDto<List<Long>>> getTopSchedules() {
        List<Long> ranking = boardRankingDao.getRanking();

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "상위 5개의 인기 일정 ID를 성공적으로 조회했습니다.", ranking));
    }

    @GetMapping("/board-cnt")
    public ResponseEntity<CommonResponseDto<Long>> getBoardCnt() {
        long totalBoards = boardService.getTotalBoardCnt();

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "전체 기사 수 조회에 성공하였습니다. 🎉", totalBoards));
    }

    @GetMapping("/approved-board-cnt")
    public ResponseEntity<CommonResponseDto<Long>> getApprovedBoardCnt() {
        long approvedBoards = boardService.getApprovedCnt();

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "승인된 기사 수 조회에 성공하였습니다. 🎉", approvedBoards));
    }

    @GetMapping("/unapproved-board-cnt")
    public ResponseEntity<CommonResponseDto<Long>> getUnapprovedBoardCnt() {
        long unapprovedBoards = boardService.getUnapprovedCnt();

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "미승인 기사 수 조회에 성공하였습니다. 🎉", unapprovedBoards));
    }

    @GetMapping("/popular-keywords")
    public ResponseEntity<CommonResponseDto<List<String>>> getPopularKeywords() {
        List<String> popular = searchKeywordService.getTopKeywords(10);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "인기 검색어 10개 조회에 성공하였습니다. 🎉", popular));
    }

}
