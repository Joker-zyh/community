package com.henu.community.aspect;


import com.henu.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Aspect
@Slf4j
public class ClearCacheAspect {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Pointcut(value = "execution(* com.henu.community.mapper.UserMapper.update*(..))")
    public void pointCut(){}

    @After("pointCut()")
    public void clearCache(JoinPoint joinPoint){
        //获取连接点参数
        Object[] args = joinPoint.getArgs();
        //获取id
        Integer id = (Integer) args[0];
        String userKey = RedisKeyUtil.getUserKey(id);
        redisTemplate.delete(userKey);
        log.info("清除缓存：" + userKey);
    }

}
