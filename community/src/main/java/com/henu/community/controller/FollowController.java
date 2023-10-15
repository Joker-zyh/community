package com.henu.community.controller;

import com.henu.community.pojo.Page;
import com.henu.community.pojo.User;
import com.henu.community.service.FollowService;
import com.henu.community.service.UserService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import com.henu.community.util.constant.EntityType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Resource
    private FollowService followService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        Integer userId = hostHolder.get().getId();
        followService.follow(userId,entityId,entityType);
        return GetJSONString.getJSONString(0,"关注成功");
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        Integer userId = hostHolder.get().getId();
        followService.unfollow(userId,entityId,entityType);
        return GetJSONString.getJSONString(0,"已取消关注");
    }

    /**
     * 我的关注
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/follower/{userId}")
    public String follower(@PathVariable("userId") int userId, Model model, Page page){
        //配置分页
        page.setLimit(5);
        page.setPath("/follower/"+ userId);
        page.setRows(Math.toIntExact(followService.findFollowerCount(userId, EntityType.ENTITY_TYPE_USER)));

        //存放被访问用户信息
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        //将当前用户对每个人的关注信息存入map中
        List<Map<String, Object>> list = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if (list != null){
            list.forEach(map -> {
                User u = (User) map.get("user");
                map.put("hasFollow",hasFollow(u.getId()));
            });
        }
        model.addAttribute("list",list);
        return "site/follower";
    }

    /**
     * 我的粉丝
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/followee/{userId}")
    public String followee(@PathVariable("userId") int userId, Model model, Page page){
        //配置分页
        page.setLimit(5);
        page.setPath("/followee/"+ userId);
        page.setRows(Math.toIntExact(followService.findFolloweeCount(userId, EntityType.ENTITY_TYPE_USER)));

        //存放被访问用户信息
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        //将当前用户对每个人的关注信息存入map中
        List<Map<String, Object>> list = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        if (list != null){
            list.forEach(map -> {
                User u = (User) map.get("user");
                map.put("hasFollow",hasFollow(u.getId()));
            });
        }
        model.addAttribute("list",list);
        return "site/followee";
    }

    private boolean hasFollow(int userId){
        if (hostHolder.get() != null){
            return followService.hasFollow(hostHolder.get().getId(), userId, EntityType.ENTITY_TYPE_USER);
        }
        return false;
    }
    
}
