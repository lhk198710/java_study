package com.example.study.api.community.board.endpoint;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import static io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS;

@Component
public class BoardArticleLikeMessageListener {

    @SqsListener(value = "board-article-like.fifo", deletionPolicy = ON_SUCCESS)
    public void receive(Message message) {
        //TODO:
    }

    record Message(
            long boardArticleId,
            long memberId,
            boolean like) {}
}
