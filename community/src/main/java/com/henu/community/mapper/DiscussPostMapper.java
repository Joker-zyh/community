package com.henu.community.mapper;

import com.henu.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPost(@Param("userId") Integer userId,
                                        @Param("page")int page,
                                        @Param("pageSize")int pageSize);

    int selectDiscussPostRows(@Param("userId") Integer userId);

    void insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(Integer id);

    void updateCommentCount(@Param("id")int id,@Param("commentCount")int commentCount);
}
