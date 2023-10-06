package com.henu.community.mapper;

import com.henu.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 添加评论：
 *      1.插入评论数据，更新评论数量（帖子表）
 *      2.添加业务
 *      3.处理请求
 *      4.修改前端页面
 */

@Mapper
public interface CommentMapper {
    List<Comment> selectPage(@Param("entityType")Integer entityType,@Param("entityId")Integer entityId,
                             @Param("offset")int offset,@Param("limit")int limit);

    int selectCommentCount(@Param("entityType")Integer entityType,@Param("entityId")Integer entityId);

    void insertComment(Comment comment);
}
