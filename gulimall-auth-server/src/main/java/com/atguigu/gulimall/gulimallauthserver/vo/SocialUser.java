package com.atguigu.gulimall.gulimallauthserver.vo;

import lombok.Data;

/**
 * 使用 Auth2 的方式 认证Token ,返回来的对象
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private int expiresIn;

    private String uid;

    private String isrealname;
}