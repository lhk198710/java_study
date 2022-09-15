package com.example.study.api.community.board;

import com.example.study.api.community.board.repository.*;
import com.example.study.api.utils.Sha512Utils;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class DocentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocentService.class);

    private final DocentQueryRepository docentQueryRepository;
    private final DocentCommandRepository docentCommandRepository;
    private final DocentLikeQueryRepository docentLikeQueryRepository;
    private final DocentLikeCommandRepository docentLikeCommandRepository;
    private final DocentFollowQueryRepository docentFollowQueryRepository;
    private final DocentFollowCommandRepository docentFollowCommandRepository;
    private final JdbcAggregateOperations jdbcAggregateOperations;
    private final QueueMessagingTemplate queueMessagingTemplate;


    public DocentService(DocentQueryRepository docentQueryRepository, DocentCommandRepository docentCommandRepository, DocentLikeQueryRepository docentLikeQueryRepository, DocentLikeCommandRepository docentLikeCommandRepository, DocentFollowQueryRepository docentFollowQueryRepositoy, DocentFollowCommandRepository docentFollowCommandRepository, JdbcAggregateOperations jdbcAggregateOperations, QueueMessagingTemplate queueMessagingTemplate) {
        this.docentQueryRepository = docentQueryRepository;
        this.docentCommandRepository = docentCommandRepository;
        this.docentLikeQueryRepository = docentLikeQueryRepository;
        this.docentLikeCommandRepository = docentLikeCommandRepository;
        this.docentFollowQueryRepository = docentFollowQueryRepositoy;
        this.docentFollowCommandRepository = docentFollowCommandRepository;
        this.jdbcAggregateOperations = jdbcAggregateOperations;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public DocentEntity joinDocent(DocentForm docentForm) {
        validateJoinDocentForm(docentForm);
        DocentEntity docentEntity = jdbcAggregateOperations.insert((docentForm.toEntity()));

        return docentEntity;
    }

    /**
     * 도슨트 휴면계정 전환
     * @param docentId
     */
    public void pauseDocent(long docentId) {
        DocentEntity docentEntity = validateDocentId(docentId);
        docentCommandRepository.docentStatusUpdate(docentEntity.docentId(), 300);
    }

    /**
     * 도슨트 탈퇴
     * @param docentId
     */
    public void deactivateDocent(long docentId) {
        DocentEntity docentEntity = validateDocentId(docentId);
        docentCommandRepository.docentStatusUpdate(docentEntity.docentId(), 400);
    }

    /**
     * 도슨트 조회
     * @param docentId
     * @return
     */
    public DocentEntity get(long docentId) {
        DocentEntity docentEntity = docentQueryRepository.findByDocentId(docentId);
        return Optional.ofNullable(docentEntity).orElseThrow(() -> new IllegalStateException("조회되는 도슨트 정보가 없습니다."));
    }

    /**
     * 도슨트 정보 업데이트
     * @param docentId
     */
    public int updateDocentInfo(long docentId, DocentModifyForm docentModifyForm) {
        DocentEntity docentEntity = validateDocentId(docentId);

        validateUpdateDocentForm(docentEntity.docentId(), docentModifyForm);

        int result = docentCommandRepository.updateDocentInfo(
                Sha512Utils.toBase64UrlEncoded(docentModifyForm.newPassword()),
                docentModifyForm.email(),
                docentModifyForm.mobile(),
                docentModifyForm.address(),
                docentModifyForm.zipcode(),
                docentModifyForm.thumbnail(),
                docentModifyForm.job(),
                docentId);

        return result;
    }

    public boolean setDocentLike(long docentId, long memberId) {
        validateDocentId(docentId);
        DocentLikeEntity search = docentLikeQueryRepository.findByDocentIdAndMemberId(docentId, memberId);

        Assert.isNull(search, "이미 좋아요를 누른 도슨트입니다.");

        jdbcAggregateOperations.insert(new DocentLikeEntity(null, docentId, memberId, LocalDateTime.now()));
        DocentLikeView docentLikeView = new DocentLikeView(docentId, memberId, true);
        sendDocentLikeQueue(docentLikeView);

        return true;
    }

    public boolean setDocentDislike(long docentId, long memberId) {
        validateDocentId(docentId);
        DocentLikeEntity search = docentLikeQueryRepository.findByDocentIdAndMemberId(docentId, memberId);

        Assert.notNull(search, "좋아요를 누른 멤버를 찾을 수 없습니다.");

        docentLikeCommandRepository.deleteDocentLike(search.docentLikeId());
        DocentLikeView docentLikeView = new DocentLikeView(docentId, memberId, false);
        sendDocentLikeQueue(docentLikeView);

        return false;
    }

    public boolean setDocentFollow(long docentId, long memberId) {
        validateDocentId(docentId);
        DocentFollowerEntity search = docentFollowQueryRepository.findByDocentIdAndMemberId(docentId, memberId);

        Assert.isNull(search, "이미 팔로우한 도슨트입니다.");

        jdbcAggregateOperations.insert(new DocentFollowerEntity(null, docentId, memberId, LocalDateTime.now()));
        DocentFollowView docentFollowView = new DocentFollowView(docentId, memberId, true);
        sendDocentFollowQueue(docentFollowView);

        return true;
    }

    public boolean setDocentUnfollow(long docentId, long memberId) {
        validateDocentId(docentId);
        DocentFollowerEntity search = docentFollowQueryRepository.findByDocentIdAndMemberId(docentId, memberId);

        Assert.notNull(search, "팔로우한 멤버를 찾을 수 없습니다.");

        docentFollowCommandRepository.docentFollowDelete(search.docentFollowId());
        DocentFollowView docentFollowView = new DocentFollowView(docentId, memberId, false);
        sendDocentFollowQueue(docentFollowView);

        return false;
    }

    private DocentEntity validateDocentId(long docentId) {
        Assert.isTrue(docentId > 0, "도슨트 조회를 위한 입력 정보를 확인해 주세요.");
        return get(docentId);
    }

    private void sendDocentLikeQueue(DocentLikeView docentLikeView) {
        queueMessagingTemplate.convertAndSend("docent-like-queue", docentLikeView);
    }

    public void sendDocentFollowQueue(DocentFollowView docentFollowView) {
        queueMessagingTemplate.convertAndSend("docent-follow-queue", docentFollowView);
    }

    public void list(String docentNickname, int status, int page, int articlesPerPage, String order, String direction) {
        Assert.isTrue(articlesPerPage <= 100, "100개를 초과해 요청할 수 없습니다.");
        Assert.isTrue(page >= 1, "페이지는 1보다 커야 합니다.");

        Pageable pageable = PageRequest.of(page - 1, articlesPerPage);

        // 1. 도슨트 아이디와 status를 이용해 게시판 카운트 조회(ref_id :: 도슨트 게시판)
        // 2. 도슨트 아이디와 status를 이용해 게시판 all 조회(ref_id :: 도슨트 게시판)
        // 3. Page<BoardArticleEntity> paged = PageableExecutionUtils.getPage({2번 조회 결과}, pageable, () -> count);
        // 4. 응답
    }

    private void validateCommonDocentForm(String password, String mobile, String email) {
        final Pattern MOBILE = Pattern.compile("01[01789]-[0-9]{3,4}-[0-9]{4}");
        final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Assert.hasLength(password, "비밀번호를 확인해 주세요.");
        Assert.isTrue(password.length() > 4, "비밀번호가 너무 짧습니다.");
        Assert.isTrue(MOBILE.matcher(mobile).matches(), "휴대전화 양식이 맞지 않습니다.");
        Assert.isTrue(EMAIL.matcher(email).matches(), "이메일 양식이 맞지 않습니다.");
    }

    private void validateJoinDocentForm(DocentForm docentForm) {
        Assert.isNull(docentQueryRepository.findByNickname(docentForm.nickname()), "이미 가입된 도슨트입니다.");
        Assert.isNull(docentQueryRepository.findByMobile(docentForm.mobile()), "이미 가입된 휴대전화입니다.");
        validateCommonDocentForm(docentForm.password(), docentForm.mobile(), docentForm.email());
    }

    private void validateUpdateDocentForm(long docentId, DocentModifyForm docentModifyForm) {
        DocentEntity docentEntity = docentQueryRepository.findByDocentId(docentId);

        Assert.isTrue(docentEntity.password().equals(Sha512Utils.toBase64UrlEncoded(docentModifyForm.oldPassword())), "비밀번호가 일치하지 않습니다.");
        validateCommonDocentForm(docentModifyForm.newPassword(), docentModifyForm.mobile(), docentModifyForm.email());
    }
}
