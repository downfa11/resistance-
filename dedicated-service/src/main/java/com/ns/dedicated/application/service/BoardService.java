package com.ns.dedicated.application.service;


import com.ns.common.UseCase;
import com.ns.dedicated.adpater.out.persistance.BoardJpaEntity;
import com.ns.dedicated.adpater.out.persistance.BoardMapper;
import com.ns.dedicated.application.port.in.*;
import com.ns.dedicated.application.port.in.command.DeleteBoardCommand;
import com.ns.dedicated.application.port.in.command.FindBoardCommand;
import com.ns.dedicated.application.port.in.command.ModifyBoardCommand;
import com.ns.dedicated.application.port.in.command.RegisterBoardCommand;
import com.ns.dedicated.application.port.out.DeleteBoardPort;
import com.ns.dedicated.application.port.out.FindBoardPort;
import com.ns.dedicated.application.port.out.ModifyBoardPort;
import com.ns.dedicated.application.port.out.RegisterBoardPort;
import com.ns.dedicated.domain.Board;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@UseCase
@Slf4j
@RequiredArgsConstructor
public class BoardService implements RegisterBoardUseCase, ModifyBoardUseCase, FindBoardUseCase, DeleteBoardUseCase {

    private final RegisterBoardPort registerBoardPort;
    private final ModifyBoardPort modifyBoardPort;
    private final DeleteBoardPort deleteBoardPort;
    private final FindBoardPort findBoardPort;
    private final BoardMapper boardMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    @CacheEvict(value="getPosts", allEntries = true)
    public Board registerBoard(RegisterBoardCommand command) {
        BoardJpaEntity jpaEntity = registerBoardPort.createBoard(
                new Board.BoardTitle(command.getTitle()),
                new Board.BoardContents(command.getContents())
        );

        Long currentDate = jpaEntity.getCreatedAt().getTime();
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("board:time",String.valueOf(currentDate),currentDate);
        log.info("register board to board zset : "+currentDate);
        return boardMapper.mapToDomainEntity(jpaEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value="getPosts", allEntries = true)
    public Board modifyBoard(ModifyBoardCommand command) {
        BoardJpaEntity jpaEntity = modifyBoardPort.modifyBoard(
                new Board.BoardId(command.getBoardId()),
                new Board.BoardTitle(command.getTitle()),
                new Board.BoardContents(command.getContents())
        );
        return boardMapper.mapToDomainEntity(jpaEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value="getPosts", allEntries = true)
    public void deleteBoard(DeleteBoardCommand command) {
        deleteBoardPort.deleteBoard(command.getBoardId());
    }

    @Override
    @Transactional
    public Board findBoard(FindBoardCommand command) {
        BoardJpaEntity entity = findBoardPort.findBoard(new Board.BoardId(command.getBoardId()));
        return boardMapper.mapToDomainEntity(entity);
    }

    @Async
    @Override
    @Cacheable(value="getPosts",key="'getPosts'+':'+ #offset")
    public List<Board> getBoardsAll(int offset) {
        List<BoardJpaEntity> boards = findBoardPort.findBoardsAll(offset);

        List<Board> boardResponses = new ArrayList<>();
        for (BoardJpaEntity board : boards)
            boardResponses.add(boardMapper.mapToDomainEntity(board));

        return boardResponses;
    }
}