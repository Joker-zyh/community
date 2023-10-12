package com.henu.community.controller.advice;

import com.henu.community.util.GetJSONString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记录日志
        logger.error("服务器发生异常：" + e.getMessage());
        //打印错误消息
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        //返回
        String xRequestWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(GetJSONString.getJSONString(1,"服务器异常"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }
}
