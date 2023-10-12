package com.henu.community.controller;

import com.henu.community.pojo.DiscussPost;
import com.henu.community.pojo.Page;
import com.henu.community.pojo.User;

import com.henu.community.service.DiscussPostService;
import com.henu.community.service.LikeService;
import com.henu.community.service.UserService;
import com.henu.community.util.constant.EntityType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(null));
        page.setPath("/index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPost(null, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPostsWithUser = new ArrayList<>();
        if (discussPosts != null){
            discussPosts.forEach(discussPost -> {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user",user);

                //查询点赞数量
                Long likeCount = likeService.likeCount(EntityType.ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount",likeCount);

                discussPostsWithUser.add(map);
            });
        }



        model.addAttribute("discussPostsWithUser",discussPostsWithUser);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }
}
