package com.henu.community.controller;

import com.henu.community.service.LikeService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Resource
    private LikeService likeService;

    @Resource
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int targetId){
        //点赞
        likeService.like(hostHolder.get().getId(),entityType,entityId,targetId);
        //获取点赞数量返回
        Long likeCount = likeService.likeCount(entityType, entityId);
        //获取点赞状态
        int likeStatus = likeService.likeStatus(hostHolder.get().getId(), entityType, entityId);
        //返回结果
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return GetJSONString.getJSONString(0,null,map);
    }
}
