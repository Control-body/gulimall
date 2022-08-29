package com.atguigu.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.util.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.gulimallauthserver.feign.MemberFeignService;
import com.atguigu.gulimall.gulimallauthserver.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/18 10:00
 *
 * @author Control.
 * @since JDK 1.8
 */
@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    MemberFeignService memberFeignService;
    @GetMapping("/oauth2.0/weibo/success")
    public  String  weibo(@RequestParam("code") String  code, HttpSession session) throws Exception {
        Map<String ,String > map=new HashMap<String ,String>();
        map.put("client_id","4234978014");
        map.put("client_secret","f77aefb724ef96efb26ef28b531e8874");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        Map<String, String> headers = new HashMap<>();
//      换取 code 码
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token","post", headers, null,map);
//      处理返回的 Token
    if(response.getStatusLine().getStatusCode()==200){
//       回去 token
        String s = EntityUtils.toString(response.getEntity());
        SocialUser socialUser = JSON.parseObject(s, SocialUser.class);
//       调用登录方法
        R login = memberFeignService.login(socialUser);
        if (login.getCode()==0){
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {});
//            保存 进session  TODO 1. 默认发的令牌 sesssion =  作用域 是 当前作用域 （解决子域 共享 问题）
//            TODO 2. 使用 JDK 的方式 比较 不容易 观察 使用 json 的方式 比较好
            session.setAttribute("loginUser",data);
            log.info("登录成功----用户{}",data.toString());
        }else {
//            登录失败 重新登录
            return "redirect:http://gulimall.com/login.html";
        }
    }else{
        return "redirect:http://gulimall.com/login.html";
    }


//        登录成功 返回首页
        return "redirect:http://gulimall.com";
    }
}
