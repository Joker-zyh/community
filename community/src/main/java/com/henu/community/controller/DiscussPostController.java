package com.henu.community.controller;

import com.henu.community.pojo.DiscussPost;
import com.henu.community.pojo.User;
import com.henu.community.service.DiscussPostService;
import com.henu.community.service.UserService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String  addDiscussPost(String title,String content){
        //添加帖子信息
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(hostHolder.get().getId());
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPostService.saveDiscussPost(discussPost);

        return GetJSONString.getJSONString(0,"帖子发布成功");
    }

    @GetMapping("/detail/{id}")
    public String getDiscussPostDetail(@PathVariable("id") Integer id, Model model){
        //获取帖子
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",post);
        // 获取发帖人信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //未完待续，回帖

        return "/site/discuss-detail";

    }
}
