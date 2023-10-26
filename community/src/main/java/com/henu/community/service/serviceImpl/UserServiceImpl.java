package com.henu.community.service.serviceImpl;

import com.henu.community.mapper.UserMapper;
import com.henu.community.pojo.User;
import com.henu.community.service.UserService;
import com.henu.community.util.RedisKeyUtil;
import com.henu.community.util.constant.ActivationStatus;
import com.henu.community.util.GenerateUUID;
import com.henu.community.util.MD5;
import com.henu.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private MailClient mailClient;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Value(value = "${community.path.domain}")
    private String domain;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    public User findUserById(Integer userId){
        //先在Redis中查找，若无则去数据库中查找
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        if (user == null){
            user = userMapper.selectUserById(userId);
            redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
        }
        return user;
    }

    /**
     * 修改用户头像
     * @param id
     * @param headerUrl
     */
    @Override
    public void updateHeaderUrl(int id, String headerUrl) {
        userMapper.updateHeaderUrlById(id,headerUrl);
    }

    /**
     * 修改密码
     * @param id
     * @param password
     */
    @Override
    public void updatePassword(int id, String password) {
        userMapper.updatePasswordById(id,password);
    }

    /**
     * 根据姓名查用户
     * @param name
     */
    @Override
    public User findUserByName(String name) {
        return userMapper.selectUserByUsername(name);
    }


    /**
     * 账号激化
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId,String code){
        //判断不同激活码，返回不同状态
        User user = userMapper.selectUserById(userId);
        if (user.getStatus() == 1){
            return ActivationStatus.ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            //激活用户，修改用户status
            userMapper.updateUserStatus(userId,1);
            return ActivationStatus.ACTIVATION_SUCCESS;
        }else {
            return ActivationStatus.ACTIVATION_FAILURE;
        }
    }

    /**
     * 注册功能
     * @param user
     * @return
     */
    @Override
    public Map<String, String> register(User user) {
        Map<String,String> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //空值处理
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
        }

        //验证账号和邮箱是否已存在
        User u = userMapper.selectUserByUsername(user.getUsername());
        if (u != null){
            map.put("usernameMsg","该账号已经存在");
            return map;
        }
        u = userMapper.selectUserByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //填充用户信息，注册
        //salt
        user.setSalt(GenerateUUID.generateUUID().substring(0,5));
        //password
        user.setPassword(MD5.md5(user.getPassword() + user.getSalt()));
        //type,status
        user.setType(0);
        user.setStatus(0);
        //activation
        user.setActivationCode(GenerateUUID.generateUUID());
        //headUrl
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/id/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",process);

        return map;
    }
}
