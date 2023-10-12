package com.henu.community.service;

import org.springframework.stereotype.Service;

@Service
public interface FollowService {
    void follow(int userId,int entityId,int entityType);

    void unfollow(int userId,int entityId,int entityType);

    Long findFollowerCount(int userId,int entityType);

    Long findFolloweeCount(int entityId,int entityType);

    boolean hasFollow(int userId,int entityId,int entityType);

}
