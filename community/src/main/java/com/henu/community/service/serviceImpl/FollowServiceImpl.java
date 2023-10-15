package com.henu.community.service.serviceImpl;

import com.henu.community.pojo.User;
import com.henu.community.service.FollowService;
import com.henu.community.service.UserService;
import com.henu.community.util.RedisKeyUtil;
import com.henu.community.util.constant.EntityType;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FollowServiceImpl implements FollowService {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserService userService;

    /**
     * 关注
     * @param userId
     * @param entityId
     * @param entityType
     */
    @Override
    public void follow(int userId, int entityId, int entityType) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //获取key
                String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityId, entityType);

                operations.multi();

                redisTemplate.opsForZSet().add(followerKey,entityId,System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followeeKey,userId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId
     * @param entityId
     * @param entityType
     */
    @Override
    public void unfollow(int userId, int entityId, int entityType) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //获取key
                String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityId, entityType);

                operations.multi();

                redisTemplate.opsForZSet().remove(followerKey,entityId);
                redisTemplate.opsForZSet().remove(followeeKey,userId);

                return operations.exec();
            }
        });
    }

    /**
     * 获取关注者数量
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public Long findFollowerCount(int userId, int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 获取某实体的粉丝数量
     * @param entityId
     * @param entityType
     * @return
     */
    @Override
    public Long findFolloweeCount(int entityId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 是否关注
     * @param userId
     * @param entityId
     * @param entityType
     * @return
     */
    @Override
    public boolean hasFollow(int userId, int entityId, int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followerKey, entityId);
        return score != null;

    }

    /**
     * 我的关注
     * @param userId 被访问的用户id
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> findFollower(int userId,int offset,int limit) {
        //获取key
        String followerKey = RedisKeyUtil.getFollowerKey(userId, EntityType.ENTITY_TYPE_USER);
        //获取ids
        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        //查询user存入map，查询关注时间存入map，将map存入list
        List<Map<String,Object>> list = new ArrayList<>();
        if (ids != null){
            ids.forEach(id ->{
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById((Integer) id);
                map.put("user",user);

                Double followTime = redisTemplate.opsForZSet().score(followerKey, user.getId());
                map.put("followTime",new Date(followTime.longValue()));
                list.add(map);

            });
        }

        return list;
    }

    /**
     * 我的粉丝
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> findFollowee(int userId,int offset,int limit) {
        //获取key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, EntityType.ENTITY_TYPE_USER);
        //获取ids
        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        //查询user存入map，查询关注时间存入map，将map存入list
        List<Map<String,Object>> list = new ArrayList<>();
        if (ids != null){
            ids.forEach(id ->{
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById((Integer) id);
                map.put("user",user);

                Double followTime = redisTemplate.opsForZSet().score(followeeKey, user.getId());
                map.put("followTime",new Date(followTime.longValue()));
                list.add(map);

            });
        }

        return list;
    }
}
