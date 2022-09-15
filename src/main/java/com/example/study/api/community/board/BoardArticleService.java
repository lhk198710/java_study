package com.example.study.api.community.board;

import com.example.study.api.community.board.repository.ArticlesCommandRepository;
import com.example.study.api.community.board.repository.ArticlesQueryRepository;
import com.example.study.api.type.Paged;
import com.example.study.api.type.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardArticleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardArticleService.class);

    private final ArticlesQueryRepository articlesQueryRepository;
    private final ArticlesCommandRepository articlesCommandRepository;
    private final JdbcAggregateOperations jdbcAggregateOperations;

    public BoardArticleService(ArticlesQueryRepository articlesQueryRepository,
                               ArticlesCommandRepository articlesCommandRepository,
                               JdbcAggregateOperations jdbcAggregateOperations) {
        this.articlesQueryRepository = articlesQueryRepository;
        this.articlesCommandRepository = articlesCommandRepository;
        this.jdbcAggregateOperations = jdbcAggregateOperations;
    }

/*
    public BoardArticleWithLikeCount getPost(Long boardId, Long boardArticleId) {
        BoardArticleEntity boardArticleEntity = articlesQueryRepository.findByBoardIdAndPostId(boardId, boardArticleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        long likeCount = 0L;//TODO:likeService.getLikeCount(LikeTargetType.POST, boardArticleId);

        Set<BoardArticleFileEntity> filteredFile = boardArticleEntity.files().stream()
                .filter(f -> Objects.equals(f.status(), "UPLOADED"))
                .collect(Collectors.toSet());

        BoardArticleEntity filteredFileBoardArticleEntity = new BoardArticleEntity(boardArticleEntity.boardArticleId(), boardArticleEntity.content(), boardArticleEntity.mediaYn(), boardArticleEntity.creator(), boardArticleEntity.status(), boardArticleEntity.boardId(), filteredFile);

        return new BoardArticleWithLikeCount(filteredFileBoardArticleEntity, likeCount);
    }

    public boolean exist(Long boardArticleId) {
        return articlesCommandRepository.existsById(boardArticleId);
    }

    @Transactional
    public long post(BoardArticleEntity newObj, List<MultipartFile> files) {
        BoardArticleEntity saved = articlesCommandRepository.save(newObj);
        Optional<BoardArticleEntity> optional = articlesQueryRepository.findById(saved.boardArticleId());
        BoardArticleEntity boardArticleEntity = optional.orElseThrow(() -> new RuntimeException("저장 실패"));

        uploadMedias(boardArticleEntity.files(), files);

        return boardArticleEntity.boardArticleId();
    }

    private void uploadMedias(Set<BoardArticleFileEntity> fileEntities, List<MultipartFile> files) {
        fileEntities.forEach(entity -> {
            Optional<MultipartFile> optional = files.stream()
                    .filter(f -> Objects.equals(f.getOriginalFilename(), entity.name()))
                    .findAny();

*/
/*
                MultipartFile file = optional.orElseThrow(() -> new RollbackException("파일 오류"));
                FileUtils.uploadFile(entity.baordArticleId(), file, entity.path());
*//*

        });
    }

    // todo : file 삭제시 저장된 경로에서 임시보관함으로 옮기거나 파일을 지우는 로직 필요.
    public BoardArticleEntity modifyPost(BoardArticleEntity boardArticleEntity) {
        BoardArticleEntity p = articlesCommandRepository.save(boardArticleEntity);
        return articlesQueryRepository.findById(p.boardArticleId()).orElseThrow(() -> new RuntimeException("수정 실패"));
    }

    public static String getSavePath() {
        return System.getProperty("user.dir") + File.separator + "files" + "/board";
    }
*/

    public Paged<BoardArticleEntity> list(long boardId, int status, int page, int articlesPerPage, String order, String direction) {
        Assert.isTrue(articlesPerPage <= 100, "100개를 초과해 요청할 수 없습니다.");
        Assert.isTrue(page >= 1, "페이지는 1보다 커야 합니다.");

        Pageable pageable = PageRequest.of(page - 1, articlesPerPage);

        Long count = articlesQueryRepository.countAllByBoardIdAndStatus(boardId, status);

        String directionDesc = Sort.Direction.DESC.toString().toLowerCase();
        List<BoardArticleEntity> list = switch (order) {
            case "like" -> {
                if (direction.equals(directionDesc)) {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByLikeDesc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                } else {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByLikeAsc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                }
            }
            case "view" -> {
                if (direction.equals(directionDesc)) {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByViewDesc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                } else {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByViewAsc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                }
            }
            default -> {
                if (direction.equals(directionDesc)) {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByCompletedDesc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                } else {
                    yield articlesQueryRepository.findAllByBoardIdAndStatusOrderByCompletedAsc(boardId, status, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
                }
            }
        };

        Page<BoardArticleEntity> paged = PageableExecutionUtils.getPage(list, pageable, () -> count);

        return Paged.of(
                paged.getTotalElements(),
                paged.getTotalPages(),
                paged.getContent());

    }

    public BoardArticleEntity get(long articleId) {
        return Optional.ofNullable(articlesQueryRepository.findByBoardArticleId(articleId))
                .orElseThrow(() -> new IllegalStateException("조회 중 에러가 발생하였습니다. 잠시 후 다시 시도해주세요."));
    }

    public long createInitial(BoardArticleForm form) {
        Assert.isTrue(form.boardId() != null, "게시판 id가 없습니다.");

        BoardArticleEntity boardArticleEntity = jdbcAggregateOperations.insert(form.toEntityInitial());
        LOGGER.info("boardArticleEntity : {}", boardArticleEntity);

        return Optional.ofNullable(boardArticleEntity).get().boardArticleId();
    }

    public void create(long articleId, BoardArticleModifyForm form) {
        // TODO: img 및 동영상 처리 여부 체크로직에 따른 status 코드 처리, writing 상태일 때 파일 사이즈로 인해 처리시간이 길어질 경우 따로 상태 변경 처리
        String check = "complete";

        int status = switch (check) {
            case "complete" -> 300;
            case "hidden" -> 410;
            case "writing" -> 200;
            default -> throw new IllegalArgumentException("잘못된 상태입니다.");
        };

        articlesCommandRepository.create(articleId, form.subject(), form.contents(), form.html(), status == 300 ? LocalDateTime.now() : null, status);
    }

    public void update(long articleId, BoardArticleModifyForm form) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");
        Assert.hasLength(form.subject(), "제목을 확인해 주세요.");
        Assert.hasLength(form.contents(), "내용을 확인해 주세요.");
        Assert.isTrue(form.html() != null, "html 여부를 확인해 주세요.");

        articlesCommandRepository.update(articleId, form.subject(), form.contents(), form.html());
    }

    public void delete(long articleId) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.updateStatus(articleId, 420);
    }

    public void complete(long articleId) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.updateStatusToComplete(articleId, 300, LocalDateTime.now());
    }

    public void hidden(long articleId) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.updateStatus(articleId, 410);
    }

    public void updateView(long articleId, Requester requester) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.createView(articleId, 2, LocalDateTime.now());
        articlesCommandRepository.updateViewCount(articleId);
    }

    public void updateLike(long articleId, Requester requester) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.createLike(articleId, 2, LocalDateTime.now());
        articlesCommandRepository.updateLikeCountByAdd(articleId);
    }

    public void cancelLike(long articleId, Requester requester) {
        Assert.isTrue(articleId > 0, "게시글 id를 확인해 주세요.");

        articlesCommandRepository.deleteLike(articleId, 2);
        articlesCommandRepository.updateLikeCountBySubtract(articleId);
    }

}
