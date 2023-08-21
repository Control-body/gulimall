package com.atguigu.gulimall.member.vo;

import lombok.Data;

/**
 * 第三方 登录返回的 用户信息
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private Long expiresIn;

    private String uid;

    private String isrealname;
}