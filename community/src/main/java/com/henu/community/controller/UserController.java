package com.henu.community.controller;

import com.henu.community.annotation.LoginRequired;
import com.henu.community.pojo.User;
import com.henu.community.service.UserService;
import com.henu.community.util.GenerateUUID;
import com.henu.community.util.HostHolder;
import com.henu.community.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value(value = "${community.path.domain}")
    private String domain;

    @Value(value = "${community.path.upload}")
    private String uploadPath;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePw(String oldPw,String newPw,String newPw2,Model model){
        //检验两次密码输入是否正确
        if (!newPw.equals(newPw2)){
            model.addAttribute("updateError","两次输入的新密码不一致");
            return "/site/setting";
        }
        //检验旧密码
        User user = hostHolder.get();
        String oldPwMD5 = MD5.md5(oldPw + user.getSalt());
        if (!oldPwMD5.equals(user.getPassword())){
            model.addAttribute("passwordError","原密码错误");
            return "/site/setting";
        }
        //可以修改
        String newPwMD5 = MD5.md5(newPw + user.getSalt());
        userService.updatePassword(user.getId(),newPwMD5);

        return "redirect:/logout";
    }

    /**
     * 更改图片：
     *      1.上传图片，声明变量multiPartFile。
     *          检验文件是否为空，文件格式是否正确
     *      2.截取图片的格式，将图片的名称拼接为本地存储的路径加uuid加格式
     *      3.新建file类，将图片资源写入file，headerImage.transferTo(file)
     *      4.更改数据库图片存储路径，将图片名称改为web路径存储
     */
    @LoginRequired
    @PostMapping("/upload")
    public String upload(MultipartFile multipartFile, Model model){
        //检验是否上传图片
        if (multipartFile == null){
            model.addAttribute("error","未选择图片");
            return "/site/setting";
        }

        // 获取图片名称
        String filename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(filename)){
            model.addAttribute("error","文件格式有误");
            return "/site/setting";
        }
        //获取图片格式
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 拼接图片新名称
        filename = GenerateUUID.generateUUID() + suffix;
        // 存入图片路径
        File image = new File(uploadPath + "/" + filename);
        //存入图片数据
        try {
            multipartFile.transferTo(image);
        } catch (IOException e) {
            logger.error("图片存储失败 "+ e.getMessage());
            throw new IllegalArgumentException("文件上传失败",e);
        }

        //图片上传成功，修改数据库,根据用户id修改headerUrl
        // http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        User user = hostHolder.get();
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";

    }

    /**
     * 浏览器展示图片：
     *      1.controller中接收到文件名
     *      2.拼接文件的本地存储路径加名称
     *      3.打开输入流，通过response写出到浏览器
     *      4.修改前端代码：
     *          * 表单提交方式
     *          * 表单提交路径
     *          * 图片文件上传格式
     *          * input的name属性
     *          * 错误提示框
     */
    @GetMapping("header/{filename}")
    public void getHeaderImage(@PathVariable("filename") String filename, HttpServletResponse response){
        //拼接本地存储路径
        String loadPath = uploadPath + "/" + filename;
        //获取图片格式
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        //response输出图片
        response.setContentType("image/" + suffix);
        try(
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(loadPath)
        ){

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,b);
            }
        }catch (Exception e){
            logger.error("头像读取失败 "+ e.getMessage());
        }


    }

    /**
     * 获取个人设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSetting(){
        return "/site/setting";
    }
}
