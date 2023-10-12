package com.henu.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";

    //实体点赞，帖子，评论
    private static final String PREFIX_ENTITY_KEY = "like:entity";

    //用户收到的赞
    private static final String PREFIX_USER_KEY = "like:user";

    //我的关注
    private static final String PREFIX_FOLLOWER = "follower";

    //某实体的粉丝
    private static final String PREFIX_FOLLOWEE = "followee";


    //实体点赞的key
    // like:entity:1/2:id --> set(ids)
    public static String getEntityLikeKey(int entityId, int entityType){
        return PREFIX_ENTITY_KEY + SPLIT + entityType + SPLIT + entityId;
    }

    //用户收到的赞的key
    //like:user:id --> number
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_KEY + SPLIT + userId;
    }

    //我的关注，follower:userId:entityType --> zSet(entityId)
    public static String getFollowerKey(int userId,int entityType){
        return PREFIX_FOLLOWER + SPLIT + userId + SPLIT + entityType;
    }

    //某实体的粉丝，followee:entityType:EntityId --> zSet(userId)
    public static String getFolloweeKey(int entityId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + entityType + SPLIT + entityId;
    }

}
