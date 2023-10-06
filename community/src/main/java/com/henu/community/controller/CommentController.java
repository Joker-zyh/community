package com.henu.community.controller;

import com.henu.community.annotation.LoginRequired;
import com.henu.community.pojo.Comment;
import com.henu.community.service.CommentService;
import com.henu.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @Resource
    private HostHolder hostHolder;

    @LoginRequired
    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable("postId")int postId, Comment comment){
        comment.setUserId(hostHolder.get().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.saveComment(comment);

        return "redirect:/discuss/detail/" + postId;
    }
}
