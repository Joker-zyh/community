package com.henu.community;

import com.henu.community.mapper.MessageMapper;
import com.henu.community.pojo.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class MessageMapperTest {
    @Resource
    private MessageMapper messageMapper;

    @Test
    public void testUpdateStatus(){
        List<Integer> list = new ArrayList<>();
        list.add(355);
        messageMapper.updateStatus(list,1);
    }

    @Test
    public void testInsert(){
        Message message = new Message(null,111,112,"111_112",
                "woooooooo",0,new Date());
        messageMapper.insertLetter(message);
    }

    @Test
    public void testSelectConversations(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 5);
        messages.forEach(System.out::println);
    }

    @Test
    public void testSelectConversationCount(){
        System.out.println(messageMapper.selectConversationCount(111));
    }

    @Test
    public void testSelectLetters(){
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 5);
        messages.forEach(System.out::println);
    }

    @Test
    public void testSelectLetterCount(){
        System.out.println(messageMapper.selectLetterCount("111_112"));
    }

    @Test
    public void testSelectUnreadLetterCount(){
        System.out.println(messageMapper.selectUnreadLettersCount(111, "111_112"));
    }

}
