package com.example.study.api.community.board.endpoint;

import com.example.study.api.community.board.*;
import com.example.study.api.result.Result;
import com.example.study.api.type.Paged;
import com.example.study.api.type.Requester;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board-articles")
class BoardArticlesController {

    private final BoardArticleService boardArticleService;
    private final BoardArticleLikeService boardArticleLikeService;

    BoardArticlesController(
            BoardArticleService boardArticleService,
            BoardArticleLikeService boardArticleLikeService) {
        this.boardArticleService = boardArticleService;
        this.boardArticleLikeService = boardArticleLikeService;
    }

    @Operation(summary = "게시글 목록 조회", description = "게시판 id 및 페이징 정보를 이용하여 목록을 조회합니다.")
    @GetMapping
    Result<Paged<BoardArticleView>> list(
            @Parameter(description = "게시판 id") @RequestParam("boardId") long boardId,
            @Parameter(description = "페이지") @RequestParam("page") int page,
            @Parameter(description = "페이지당 글 수") @RequestParam("articlesPerPage") int articlesPerPage,
            @Parameter(description = "정렬", schema = @Schema(allowableValues = {"completed", "view", "like"})) @RequestParam(value = "order", required = false, defaultValue = "completed") String order,
            @Parameter(description = "순서", schema = @Schema(allowableValues = {"desc", "asc"})) @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction) {
        return Result.success(
                boardArticleService.list(boardId, 300, page, articlesPerPage, order, direction)
                        .map(BoardArticleView::of));
    }

    @Operation(summary = "게시글 조회", description = "게시글 id를 이용하여 목록을 조회합니다.")
    @GetMapping("/{articleId}")
    Result<BoardArticleView> get(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId) {
        return Result.success(
                BoardArticleView.of(boardArticleService.get(articleId)));
    }

    @Operation(summary = "게시글 최초 등록", description = "게시글을 최초 등록합니다.<br>최초 등록 시 게시글 상태는 작성중(200) 입니다.<br>등록 성공 후, 게시글 번호를 리턴합니다.")
    @PostMapping
    Result<Long> createInitial(
            @Parameter(description = "게시글 정보") @RequestBody BoardArticleForm form) {
        return Result.success(boardArticleService.createInitial(form));
    }

    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다.")
    @PostMapping("/{articleId}")
    Result<Void> create(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId,
            @Parameter(description = "게시글 정보") @RequestBody BoardArticleModifyForm form) {
        boardArticleService.create(articleId, form);
        return Result.successWithoutBody();

    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/{articleId}")
    Result<Void> update(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId,
            @Parameter(description = "게시글 정보") @RequestBody BoardArticleModifyForm form) {
        boardArticleService.update(articleId, form);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 상태 수정 - 완료", description = "게시글 상태를 완료(300)로 수정합니다.")
    @PatchMapping("/{articleId}/complete")
    Result<Void> complete(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId) {
        boardArticleService.complete(articleId);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 상태 수정 - 숨김", description = "게시글 상태를 숨김(410)으로 수정합니다.")
    @PatchMapping("/{articleId}/hidden")
    Result<Void> hidden(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId) {
        boardArticleService.hidden(articleId);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 삭제", description = "게시글 상태를 삭제대상(420)으로 수정합니다.")
    @DeleteMapping("/{articleId}")
    Result<Void> delete(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId) {
        boardArticleService.delete(articleId);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 조회수 증가", description = "게시글의 조회수를 증가시킨다.")
    @PatchMapping("/{articleId}/view")
    Result<Void> view(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId, Requester requester) {
        boardArticleService.updateView(articleId, requester);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 누른다.")
    @PatchMapping("/{articleId}/like")
    Result<Void> like(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId, Requester requester) {
        boardArticleService.updateLike(articleId, requester);
        return Result.successWithoutBody();
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글의 좋아요를 취소한다.")
    @DeleteMapping("/{articleId}/like")
    Result<Void> cancelLike(
            @Parameter(description = "게시글 id") @PathVariable("articleId") long articleId, Requester requester) {
        boardArticleService.cancelLike(articleId, requester);
        return Result.successWithoutBody();
    }

}
