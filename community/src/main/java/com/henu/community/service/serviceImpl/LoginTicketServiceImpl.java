package com.henu.community.service.serviceImpl;

import com.henu.community.mapper.UserMapper;
import com.henu.community.pojo.LoginTicket;
import com.henu.community.pojo.User;
import com.henu.community.service.LoginTicketService;
import com.henu.community.util.ExpiredTime;
import com.henu.community.util.GenerateUUID;
import com.henu.community.util.MD5;
import com.henu.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录的过程：
 *      1.在Service层中，判断空值，检验账号，密码，激活状态。插入LoginTicket，返回
 *      2.在controller中，根据map是否包含ticket判断是否登陆成功
 *          1.登陆成功后，将ticket存入Cookie，跳转首页
 *          2.登陆失败在Model里加入失败信息，返回登陆界面。
 *      3.前端修改：
 *          1.默认值，登陆时输入的账号密码，够选的框，失败后还保留
 *          2.修改失败后的提示信息，动态显示
 *          3.修改登陆失败提示框，根据是否有错误信息显示。
 */
@Service
public class LoginTicketServiceImpl implements LoginTicketService {
    @Resource
    private UserMapper userMapper;

    /*@Resource
    private LoginTicketMapper loginTicketMapper;*/

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 根据ticket令牌获取登陆信息
     * @param ticket
     * @return
     */
    public LoginTicket getLoginTicket(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    /**
     * 退出
     * @param ticket
     */
    public void logout(String ticket){
        /*loginTicketMapper.updateStatusByTicket(ticket,1);*/
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        if (loginTicket != null){
            loginTicket.setStatus(1);
            redisTemplate.opsForValue().set(ticketKey,loginTicket);
        }
    }

    /**
     * 登陆
     * @param username
     * @param password
     * @param rememberMe
     * @return
     */
    @Override
    public Map<String, String> login(String username, String password, boolean rememberMe) {
        Map<String,String> map = new HashMap<>();

        //验证空值
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //校验账号，激活状态，密码
        User user = userMapper.selectUserByUsername(username);
        if (user == null){
            map.put("usernameMsg","账号不存在");
            return map;
        }

        //检验账号激活状态
        if (user.getStatus() == 0){
            map.put("usernameMsg","当前用户没有激活，请点击邮件链接");
            return map;
        }

        //密码要加盐后加密验证
        password = MD5.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())){
            map.put("passwordMsg","密码错误");
            return map;
        }


        //登陆，生成令牌，将信息存入数据库,返回ticket
        long expired = rememberMe? ExpiredTime.LONG_SECONDS : ExpiredTime.DEFAULT_SECONDS;

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(GenerateUUID.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expired * 1000));
        /*loginTicketMapper.insertLoginTicket(loginTicket);*/

        //生成key，存储
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }
}
