package com.henu.community.mapper;

import com.henu.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查询当前用户的会话（每个会话显示最新的消息）
    List<Message> selectConversations(@Param("userId") int userId,
                                      @Param("offset") int offset,@Param("limit") int limit);

    // 查询当前用户的会话个数（用于分页）
    int selectConversationCount(int userId);

    // 查询当前用户与对方用户的会话中的所有消息
    List<Message> selectLetters(@Param("conversationId") String conversationId,
                                @Param("offset") int offset,@Param("limit") int limit);

    //查询当前用户与对方用户的会话中的所有消息的个数（用于分页）
    int selectLetterCount(@Param("conversationId") String conversationId);

    // 查询当前用户（在当前会话）中的未读消息个数
    int selectUnreadLettersCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    //插入信息
    void insertLetter(Message message);

    //更新消息状态，已读，删除
    void updateStatus(@Param("ids") List<Integer> ids,@Param("status") int status);

}
