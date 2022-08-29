package com.atguigu.gulimall.ssoservertest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/21 9:58
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/login.html") // 子系统都来这
    public String loginPage(@RequestParam(value = "url") String url,
                            Model model,
                            @CookieValue(value = "sso_token", required = false) String redisKey) {
        // 非空代表就登录过了
        if (!StringUtils.isEmpty(redisKey)) {
            // 告诉子系统他的redisKey，拿着该token就可以查redis了
            return "redirect:" + url + "?token=" + redisKey;
        }
        model.addAttribute("url", url);

        // 子系统都没登录过才去登录页
        return "login";
    }
    @PostMapping("/doLogin")
    public  String doLogin(String username, String password,String  url,Model model,
                           HttpServletResponse response){
        if(!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(password)){
//            登录成功
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid,username);
            Cookie ssoToken = new Cookie("sso_token", uuid);
            response.addCookie(ssoToken);
            return "redirect:"+url+"?token="+uuid;
        }else{
            model.addAttribute("url", url);
            return "login";
        }
    }
}
