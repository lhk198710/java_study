package com.example.study.api.community.board.endpoint;

import com.example.study.api.community.board.DocentForm;
import com.example.study.api.community.board.DocentModifyForm;
import com.example.study.api.community.board.DocentService;
import com.example.study.api.community.board.DocentView;
import com.example.study.api.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/docents")
public class DocentsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocentsController.class);

    private DocentService docentService;

    public DocentsController(DocentService docentService) {
        this.docentService = docentService;
    }

    @PostMapping
    @Operation(summary="도슨트 등록", description="도슨트 가입 API입니다.")
    Result<Void> joinDocent(@Parameter(description = "도슨트 정보") @RequestBody DocentForm docentForm) {
        docentService.joinDocent(docentForm);
        return Result.successWithoutBody();
    }

    @GetMapping("/{docentId}")
    @Operation(summary="도슨트 조회", description="도슨트의 정보를 조회하는 API입니다.")
    Result<DocentView> searchDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId) {
        return Result.success(DocentView.of(docentService.get(docentId)));
    }

    @PutMapping("/{docentId}")
    @Operation(summary="도슨트 수정", description="도슨트의 정보를 수정하는 API입니다.")
    Result<Integer> updateDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId,
                              @Parameter(description = "docents 정보 수정폼") @RequestBody DocentModifyForm docentModifyForm) {
        int result = docentService.updateDocentInfo(docentId, docentModifyForm);
        return Result.success(result);
    }

    @DeleteMapping("/{docentId}")
    @Operation(summary="도슨트 탈퇴", description="등록된 도슨트의 활성화 상태를 비활성화로 바꾸는 API입니다.")
    Result<Void> deactivateDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId) {
        docentService.deactivateDocent(docentId);
        return Result.successWithoutBody();
    }

    @PostMapping("/{docentId}/like")
    @Operation(summary="도슨트 좋아요", description="도슨트에게 좋아요를 보내는 API입니다.")
    Result<Boolean> likeDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId,
                            @Parameter(description = "member 테이블의 member_id") @RequestParam("memberId") long memberId) {
        // TODO : Requester를 이용해 사용자 ID, 권한 획득 후 작업하는 방식으로 변경 필요.
        boolean docentLikeView = docentService.setDocentLike(docentId, memberId);
        return Result.success(docentLikeView);
    }

    @DeleteMapping("/{docentId}/like")
    @Operation(summary="도슨트 좋아요 취소", description="도슨트에게 좋아요 취소를 보내는 API입니다.")
    Result<Boolean> dislikeDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId,
                               @Parameter(description = "member 테이블의 member_id") @RequestParam("memberId") long memberId) {
        // TODO : Requester를 이용해 사용자 ID, 권한 획득 후 작업하는 방식으로 변경 필요.
        boolean docentLikeView = docentService.setDocentDislike(docentId, memberId);
        return Result.success(docentLikeView);
    }

    @PostMapping("/{docentId}/follow")
    @Operation(summary="도슨트 팔로우", description="도슨트를 팔로우 하는 API입니다.")
    Result<Boolean> followDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId,
                              @Parameter(description = "member 테이블의 member_id") @RequestParam("memberId") long memberId) {
        // TODO : Requester를 이용해 사용자 ID, 권한 획득 후 작업하는 방식으로 변경 필요.
        boolean docentFollowView = docentService.setDocentFollow(docentId, memberId);
        return Result.success(docentFollowView);
    }

    @DeleteMapping("/{docentId}/follow")
    @Operation(summary="도슨트 언팔로우", description="도슨트를 언팔로우 하는 API입니다.")
    Result<Boolean> unfollowDocent(@Parameter(description = "docents 테이블의 docent_id") @PathVariable("docentId") long docentId,
                                 @Parameter(description = "member 테이블의 member_id") @RequestParam("memberId") long memberId) {
        // TODO : Requester를 이용해 사용자 ID, 권한 획득 후 작업하는 방식으로 변경 필요.
        boolean docentFollowView = docentService.setDocentUnfollow(docentId, memberId);
        return Result.success(docentFollowView);
    }

    @ApiIgnore
    @GetMapping("/{docentId}/articles")
    @Operation(summary="도슨트 커뮤니티 글 조회", description="도슨트 커뮤니티의 글들을 조회하는 API입니다.")
    Result<Void> searchDocentArticle() {
        return Result.successWithoutBody();
    }

    @Deprecated
    @ApiIgnore
    @PutMapping("/{docentId}/space/like")
    @Operation(summary="도슨트 공간 좋아요", description="도슨트 공간에 좋아요를 보내는 API입니다.")
    Result<Void> likeDocentSpace() {
        return Result.successWithoutBody();
    }

    @Deprecated
    @ApiIgnore
    @PutMapping("/{docentId}/space/dislike")
    @Operation(summary="도슨트 공간 좋아요 취소", description="도슨트 공간에 보낸 좋아요를 취소하는 API입니다.")
    Result<Void> dislikeDocentSpace() {
        return Result.successWithoutBody();
    }
}
