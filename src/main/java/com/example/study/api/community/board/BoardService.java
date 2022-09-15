package com.example.study.api.community.board;

import com.example.study.api.community.board.repository.BoardsCommandsRepository;
import com.example.study.api.community.board.repository.BoardsRepository;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardsRepository boardsRepository;
    private final BoardsCommandsRepository boardsCommandsRepository;
    private final JdbcAggregateOperations jdbcAggregateOperations;

    public BoardService(BoardsRepository boardsRepository,
                        BoardsCommandsRepository boardsCommandsRepository,
                        JdbcAggregateOperations jdbcAggregateOperations) {
        this.boardsRepository = boardsRepository;
        this.boardsCommandsRepository = boardsCommandsRepository;
        this.jdbcAggregateOperations = jdbcAggregateOperations;
    }

    public BoardEntity get(long boardId) {
        return Optional.ofNullable(boardsRepository.findByBoardId(boardId))
                .orElseThrow(() -> new IllegalArgumentException("게시판이 존재하지 않습니다."));
    }

    public List<BoardEntity> list(int status) {
        return boardsRepository.findAllByStatus(status);
    }

    public void create(BoardForm form) {
        Assert.hasLength(form.boardCode(), "게시판 코드를 확인해 주세요.");
        Assert.hasLength(form.name(), "게시판 이름을 확인해 주세요.");
        Assert.isTrue(form.status() > 0, "게시판 상태를 확인해 주세요.");

        jdbcAggregateOperations.insert(form.toEntity());
    }

    public void update(long boardId, BoardModifyForm form) {
        Assert.isTrue(boardId > 0, "게시판 id를 확인해 주세요.");
        Assert.hasLength(form.boardCode(), "게시판 코드를 확인해 주세요.");
        Assert.hasLength(form.name(), "게시판 이름을 확인해 주세요.");
        Assert.isTrue(form.status() > 0, "게시판 상태를 확인해 주세요.");

        boardsCommandsRepository.update(boardId, form.boardCode(), form.name(), form.status());
    }

    public void delete(long boardId) {
        boardsCommandsRepository.updateStatus(boardId, 420);
    }

}
