package com.example.study.api.community.board.endpoint;

import com.example.study.api.community.board.BoardForm;
import com.example.study.api.community.board.BoardModifyForm;
import com.example.study.api.community.board.BoardService;
import com.example.study.api.community.board.BoardView;
import com.example.study.api.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/boards")
class BoardsController {

    private final BoardService boardService;

    BoardsController(BoardService boardService) {
        this.boardService = boardService;
    }

    @Operation(summary = "게시판 목록 조회", description = "게시판 목록을 조회합니다.(페이징 처리 x)")
    @GetMapping
    Result<List<BoardView>> list() {
        return Result.success(boardService.list(300).stream().map(BoardView::of).collect(Collectors.toList()));
    }

    @Operation(summary = "게시판 조회", description = "게시판 id를 이용하여 게시판을 조회합니다.")
    @GetMapping("/{boardId}")
    Result<BoardView> get(
            @Parameter(description = "게시판 id") @PathVariable("boardId") long boardId) {
        return Result.success(BoardView.of(boardService.get(boardId)));
    }

    @Operation(summary = "게시판 등록", description = "게시판을 등록합니다.")
    @PostMapping
    Result<Void> create(
            @Parameter(description = "게시판 정보") @RequestBody BoardForm form) {
        boardService.create(form);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시판 수정", description = "게시판을 수정합니다.")
    @PutMapping("/{boardId}")
    Result<Void> update(
            @Parameter(description = "게시판 id") @PathVariable("boardId") long boardId,
            @Parameter(description = "게시판 정보") @RequestBody BoardModifyForm form) {
        boardService.update(boardId, form);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    Result<Void> delete(
            @Parameter(description = "게시판 id") @PathVariable("boardId") long boardId) {
        boardService.delete(boardId);
        return Result.successWithoutBody();
    }

}
