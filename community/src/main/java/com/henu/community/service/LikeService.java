package com.henu.community.service;

import org.springframework.stereotype.Service;


@Service
public interface LikeService {
    void like(int userId,int entityType,int entityId,int targetId);

    Long likeCount(int entityType,int entityId);

    int likeStatus(int userId, int entityType,int entityId);

    int findUserLikeCount(int userId);
}
