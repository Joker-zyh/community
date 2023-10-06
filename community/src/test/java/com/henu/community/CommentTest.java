package com.henu.community;

import com.henu.community.mapper.CommentMapper;
import com.henu.community.pojo.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
public class CommentTest {
    @Resource
    private CommentMapper commentMapper;

    @Test
    public void testInsert(){
        Comment comment = new Comment();
        comment.setContent("111");
        comment.setEntityType(1);
        comment.setEntityId(1);
        comment.setStatus(0);
        comment.setTargetId(1);
        comment.setCreateTime(new Date());
        comment.setUserId(12);
        commentMapper.insertComment(comment);
    }

    @Test
    public void testSelectPage(){
        System.out.println(commentMapper.selectPage(1, 233, 0, 5));
    }

    @Test
    public void testSelectCommentCount(){
        System.out.println(commentMapper.selectCommentCount(1, 233));
    }
}
