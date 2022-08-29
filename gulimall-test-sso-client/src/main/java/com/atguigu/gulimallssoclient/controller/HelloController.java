package com.atguigu.gulimallssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/21 9:33
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    private String ssoServer;

    /*** 无需登录就可访问*/
    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        return "hello"; }


    @GetMapping("/employees")
    public  String employees(Model model, HttpSession session,
                             @RequestParam(value="token",required=false) String token) {
        if(!StringUtils.isEmpty(token)){
            session.setAttribute("loginUser","zahngsan");
        }
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser==null){
//            没有登录 跳转认证 服务器
            return "redirect:"+ssoServer+"?url=http://client1.com:8081/employees";
        }else{
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps", emps);
            model.addAttribute("username","zahngsan");
            return "list";

        }
    }
}
