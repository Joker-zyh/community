package com.henu.community.controller;

import com.henu.community.service.FollowService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class FollowController {
    @Resource
    private FollowService followService;

    @Resource
    private HostHolder hostHolder;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        Integer userId = hostHolder.get().getId();
        followService.follow(userId,entityId,entityType);
        return GetJSONString.getJSONString(0,"关注成功");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        Integer userId = hostHolder.get().getId();
        followService.unfollow(userId,entityId,entityType);
        return GetJSONString.getJSONString(0,"已取消关注");
    }
}
