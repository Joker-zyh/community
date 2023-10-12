package com.henu.community.controller;

import com.henu.community.annotation.LoginRequired;
import com.henu.community.pojo.Comment;
import com.henu.community.pojo.DiscussPost;
import com.henu.community.pojo.Page;
import com.henu.community.pojo.User;
import com.henu.community.service.CommentService;
import com.henu.community.service.DiscussPostService;
import com.henu.community.service.LikeService;
import com.henu.community.service.UserService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import com.henu.community.util.constant.EntityType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;
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

    /**
     * 展示帖子评论，及评论的回复
     *      1.查询该帖子的评论，将评论和评论人,回复，回复人和被回复人作为一个map装到集合中。
     *      2.查询评论的回复，将回复和回复人及被回复人作为一个map装到集合中，装入帖子评论的map中。
     *      3.结构：
     *          一个帖子-》n个评论
     *          一个评论-》一个评论人，一个评论内容，n个回复，回复数量
     *          一个回复-》一个回复内容，一个回复人，一个被回复人
     */
    @GetMapping("/detail/{id}")
    public String getDiscussPostDetail(@PathVariable("id") Integer id, Model model, Page page){
        //获取帖子
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",post);
        // 获取发帖人信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //获取帖子点赞个数
        Long likeCount = likeService.likeCount(EntityType.ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount",likeCount);
        //用户点赞状态
        int likeStatus = hostHolder.get() == null?0:likeService.likeStatus(hostHolder.get().getId(),EntityType.ENTITY_TYPE_POST,id);
        model.addAttribute("likeStatus",likeStatus);

        //未完待续，回帖
        //完整评论分页
        page.setPath("/discuss/detail/" + id);
        page.setRows(post.getCommentCount());
        //分页查询
        List<Comment> comments = commentService.findPage(EntityType.ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());
        //评论有多条，一条一个map。评论的ViewObject列表
        List<Map<String,Object>> commentVOList = new ArrayList<>();
        //一条评论一条map，将每条评论的内容和发布人存入
        if (comments.size() > 0){
            comments.forEach(comment -> {
                Map<String,Object> map = new HashMap<>();
                map.put("comment",comment);
                User commentUser = userService.findUserById(comment.getUserId());
                map.put("user",commentUser);

                //获取评论点赞个数
                Long likeCount2 = likeService.likeCount(EntityType.ENTITY_TYPE_COMMENT, comment.getId());
                map.put("likeCount",likeCount2);
                //用户点赞状态
                int likeStatus2 = hostHolder.get() == null?0:likeService.likeStatus(hostHolder.get().getId(),EntityType.ENTITY_TYPE_COMMENT,comment.getId());
                map.put("likeStatus",likeStatus2);

                //评论的回复也得存入,不需要分页
                List<Comment> replys = commentService.findPage(EntityType.ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                //回复有多条，一条回复一个map，存回复的信息，回复人和被回复人
                List<Map<String,Object>> replyVOList = new ArrayList<>();
                if (replys.size() > 0){
                    replys.forEach(reply ->{
                        Map<String,Object> replyMap = new HashMap<>();
                        //存回复
                        replyMap.put("replyComment",reply);
                        //存回复人
                        replyMap.put("replyUser",userService.findUserById(reply.getUserId()));
                        //存被回复人
                        /*User target = null;
                        if (reply.getTargetId() != null || reply.getTargetId() != 0){
                            target = userService.findUserById(reply.getTargetId());
                        }*/

                        User target = reply.getTargetId() == 0?null:userService.findUserById(reply.getTargetId());
                        replyMap.put("target",target);

                        Long likeCount3 = likeService.likeCount(EntityType.ENTITY_TYPE_COMMENT, reply.getId());
                        replyMap.put("likeCount",likeCount3);
                        //用户点赞状态
                        int likeStatus3 = hostHolder.get() == null?0:likeService.likeStatus(hostHolder.get().getId(),EntityType.ENTITY_TYPE_COMMENT,reply.getId());
                        replyMap.put("likeStatus",likeStatus3);
                        //将回复信息存入map
                        replyVOList.add(replyMap);
                    });
                }

                //将回复信息存入map
                map.put("replys",replyVOList);

                //每个评论有回复数量
                int replyCount = commentService.findCommentCount(EntityType.ENTITY_TYPE_COMMENT, comment.getId());
                map.put("replyCount",replyCount);

                //将评论信息map存入集合
                commentVOList.add(map);

            });
        }
        //将评论集合存入model
        model.addAttribute("comments",commentVOList);

        return "/site/discuss-detail";

    }
}
