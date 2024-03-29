package com.atguigu.gulimall.gulimallthirdparty.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallthirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/12 10:02
 *
 * @author Control.
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController{
    @Autowired
    SmsComponent smsComponent;
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone")String phone, @RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
         return R.ok();
    }
}
