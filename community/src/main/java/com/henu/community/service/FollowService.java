package com.henu.community.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FollowService {
    void follow(int userId,int entityId,int entityType);

    void unfollow(int userId,int entityId,int entityType);

    Long findFollowerCount(int userId,int entityType);

    Long findFolloweeCount(int entityId,int entityType);

    boolean hasFollow(int userId,int entityId,int entityType);

    //获取我的关注
    List<Map<String,Object>> findFollower(int userId,int offset,int limit);

    //获取我的粉丝
    List<Map<String,Object>> findFollowee(int userId,int offset,int limit);

}
