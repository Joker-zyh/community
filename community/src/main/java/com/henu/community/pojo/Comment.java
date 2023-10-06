package com.henu.community.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {
    private Integer id;
    private Integer userId;
    //被评论实体，例如帖子，评论
    private Integer entityType;
    //被评论实体id
    private Integer entityId;
    // 被回复人id
    private Integer targetId;
    private String content;
    private Integer status;
    private Date createTime;
}
