package com.henu.community.service.serviceImpl;

import com.henu.community.service.LikeService;
import com.henu.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeServiceImpl implements LikeService{
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void like(int userId, int entityType, int entityId,int targetId) {
        //使用事务
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //获取key
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityId, entityType);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(targetId);

                //判断点赞状态
                Boolean isLike = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                redisTemplate.multi();
                //判断用户是否点赞，若已点赞则取消，若未点赞则点赞
                if (Boolean.TRUE.equals(isLike)){
                    //已点赞，取消
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else {
                    //未点赞，点赞
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisTemplate.exec();
            }
        });


    }

    /**
     * 点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Long likeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityId, entityType);
        return redisTemplate.opsForSet().size(likeKey);
    }

    /**
     * 是否点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public int likeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityId, entityType);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey,userId))?1:0;
    }

    /**
     * 用户收到的赞
     * @param userId
     * @return
     */
    @Override
    public int findUserLikeCount(int userId) {
        String key = RedisKeyUtil.getUserLikeKey(userId);
        Integer integer = (Integer) redisTemplate.opsForValue().get(key);
        return integer == null?0:integer;
    }
}
