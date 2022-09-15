package com.example.study.api.community.board.endpoint;

import com.example.study.api.community.board.DocentFollowView;
import com.example.study.api.community.board.DocentLikeView;
import com.example.study.api.community.board.repository.DocentCommandRepository;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS;

@Component
public class DocentLikeAndFollowCountListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocentLikeAndFollowCountListener.class);

    DocentCommandRepository docentCommandRepository;

    public DocentLikeAndFollowCountListener(DocentCommandRepository docentCommandRepository) {
        this.docentCommandRepository = docentCommandRepository;
    }

    @SqsListener(value = "docent-like-queue", deletionPolicy = ON_SUCCESS)
    public void receiveDocentLikeQueue(DocentLikeView docentLikeView) {
        LOGGER.info("message: {}", docentLikeView);

        if(docentLikeView.like()) {
            docentCommandRepository.docentLike(docentLikeView.docentId());
        } else {
            docentCommandRepository.docentDisLike(docentLikeView.docentId());
        }
    }

    @SqsListener(value = "docent-follow-queue", deletionPolicy = ON_SUCCESS)
    public void receiveDocentFollowQueue(DocentFollowView docentFollowView) {
        LOGGER.info("message: {}", docentFollowView);

        if(docentFollowView.follow()) {
            docentCommandRepository.docentFollow(docentFollowView.docentId());
        } else {
            docentCommandRepository.docentUnfollow(docentFollowView.docentId());
        }
    }
}
