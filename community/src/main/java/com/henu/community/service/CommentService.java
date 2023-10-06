package com.henu.community.service;

import com.henu.community.mapper.CommentMapper;
import com.henu.community.pojo.Comment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public interface CommentService {
    List<Comment> findPage(int entityType,int entityId,int offset,int limit);

    int findCommentCount(int entityType,int entityId);

    void saveComment(Comment comment);

}
