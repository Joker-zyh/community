package com.henu.community.controller;

import com.henu.community.mapper.MessageMapper;
import com.henu.community.pojo.Message;
import com.henu.community.pojo.Page;
import com.henu.community.pojo.User;
import com.henu.community.service.MessageService;
import com.henu.community.service.UserService;
import com.henu.community.util.GetJSONString;
import com.henu.community.util.HostHolder;
import com.henu.community.util.SensitiveFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/letter")
public class MessageController {
    @Resource
    private MessageService messageService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private SensitiveFilter sensitiveFilter;

    /**
     * 私信列表
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/list")
    public String getConversations(Model model, Page page){
        Integer userId = hostHolder.get().getId();
        //给分页设置
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(userId));
        page.setPath("/letter/list");

        //查询会话，及其他页面所需信息，并加入Model
        List<Message> conversationList = messageService.findConversations(userId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            conversationList.forEach(conversation -> {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",conversation);
                //存和当前用户发私信的用户信息
                int fromId = userId.equals(conversation.getFromId()) ? conversation.getToId() : conversation.getFromId();
                map.put("fromUserInfo",userService.findUserById(fromId));
                //拼接conversationId
                String conversationId = Math.min(userId,fromId) + "_"+ Math.max(userId,fromId);
                //存未读信息数量
                map.put("unreadLetterCount",messageService.findUnreadLettersCount(userId,conversationId));
                //存会话所有信息数量
                map.put("letterCount",messageService.findLetterCount(conversationId));
                conversations.add(map);
            });
        }
        //查询未读消息数量
        model.addAttribute("unreadConversationCount",messageService.findUnreadLettersCount(userId,null));

        model.addAttribute("conversations",conversations);
        return "/site/letter";
    }


    /**
     * 会话详情
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/detail/{conversationId}")
    public String getConversationDetail(@PathVariable("conversationId") String conversationId,Model model, Page page){
        //配置分页
        page.setLimit(Integer.MAX_VALUE);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //查询某一会话的消息，并同发消息人的信息一同存入map
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        //封装数据
        if (letterList != null){
            letterList.forEach(letter -> {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                //将发消息人信息存入
                User user = userService.findUserById(letter.getFromId());
                if (!user.getId().equals(hostHolder.get().getId())){
                    map.put("fromUser",user);
                    map.put("toUser",null);
                }else {
                    map.put("fromUser",null);
                    map.put("toUser",user);
                }

                letters.add(map);
            });
        }
        model.addAttribute("letters",letters);
        //存入当前用户会话对象的信息
        model.addAttribute("target",getFromLetterUser(conversationId));

        //将未读设为已读
        List<Integer> unreadLetterIds = getUnreadLetterIds(letterList);
        if (!unreadLetterIds.isEmpty()){
            messageService.updateStatus(unreadLetterIds,1);
        }

        return "/site/letter-detail";
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteLetter(int id){
        List<Integer> list = new ArrayList<>();
        list.add(id);
        messageService.updateStatus(list,2);
        return GetJSONString.getJSONString(0);
    }

    /**
     * 得到未读消息id
     * @param messages
     * @return
     */
    private List<Integer> getUnreadLetterIds(List<Message> messages){
        if (messages == null){
            return null;
        }
        List<Integer> ids = new ArrayList<>();
        messages.forEach(message -> {
            if ((message.getStatus() == 0) && message.getToId().equals(hostHolder.get().getId())){
                ids.add(message.getId());
            }
        });
        return ids;
    }

    /**
     * 得到当前用户会话对象的信息
     * @param conversationId
     * @return
     */
    private User getFromLetterUser(String conversationId){
        String[] ids = conversationId.split("_");
        if (hostHolder.get().getId() == Integer.parseInt(ids[0])){
            return userService.findUserById(Integer.parseInt(ids[1]));
        }else {
            return userService.findUserById(Integer.parseInt(ids[0]));
        }
    }

    /**
     * 发送私信
     * @param toName
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addLetter(String toName,String content){
        //填充数据
        Message message = new Message();
        //敏感词过滤
        message.setContent(HtmlUtils.htmlEscape(content));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        User toUser = userService.findUserByName(toName);
        if (toUser == null){
            return GetJSONString.getJSONString(0,"没有找到该用户！");
        }

        int fromUserId = hostHolder.get().getId();
        int toUserId = toUser.getId();
        message.setFromId(fromUserId);
        message.setToId(toUserId);
        message.setStatus(0);
        message.setConversationId(Math.min(fromUserId,toUserId) + "_" + Math.max(fromUserId,toUserId));
        message.setCreateTime(new Date());
        //添加
        messageService.saveLetter(message);
        return GetJSONString.getJSONString(1,"发送成功！");
    }








}
