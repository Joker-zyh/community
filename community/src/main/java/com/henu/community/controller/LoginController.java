package com.henu.community.controller;

import com.google.code.kaptcha.Producer;
import com.henu.community.pojo.User;
import com.henu.community.service.LoginTicketService;
import com.henu.community.service.UserService;
import com.henu.community.util.ActivationStatus;
import com.henu.community.util.ExpiredTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private LoginTicketService loginTicketService;

    @Resource
    private Producer kaptchaProducer;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    /**
     * 退出
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        loginTicketService.logout(ticket);
        return "redirect:/login";
    }

    /**
     *     2.在controller中，根据map是否包含ticket判断是否登陆成功
     *          1.登陆成功后，将ticket存入Cookie，跳转首页
     *          2.登陆失败在Model里加入失败信息，返回登陆界面。
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberMe,
                        Model model,HttpSession session,HttpServletResponse response){
        //判断验证码
        String kaptchaCode = (String) session.getAttribute("kaptha");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(kaptchaCode) || !kaptchaCode.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }

        Map<String, String> map = loginTicketService.login(username, password, rememberMe);
        if (map.containsKey("ticket")){
            //登陆成功
            Cookie cookie = new Cookie("ticket",map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(rememberMe? ExpiredTime.LONG_SECONDS : ExpiredTime.DEFAULT_SECONDS);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            //登陆失败，信息错误，重回登陆界面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 生成图片验证码
     * @param response
     * @param session
     */
    @GetMapping("/kaptcha")
    public void getKaptchaImage(HttpServletResponse response, HttpSession session){
        //生成验证码，生成图片，将验证码放入图片
        //将验证码放入session
        //将图片写入响应
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        session.setAttribute("kaptha",text);

        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }

    }


    /**
     * 跳转到注册页面
     * @return
     */
    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    /**
     * 跳转到登陆页面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    /**
     * 注册功能
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, String> map = userService.register(user);
        //map为空证明前边操作没有异常，数据正确，可注册
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，请在我们发送的邮件里点击链接，激活账号。");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            //有错误，弹出错误信息
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活账号
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        //无论是否成功均来到中间页面提示后再跳转
        //成功跳转到登陆界面
        //重复和失败最终均跳转到首页
        int activation = userService.activation(userId, code);
        if (activation == ActivationStatus.ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，请前往登陆页面登陆。");
            model.addAttribute("target","/login");
        }else if (activation == ActivationStatus.ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，重复激活。");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，激活码错误");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
