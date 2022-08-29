package com.atguigu.gulimall.member;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GulimallMemberApplicationTests {
    @Autowired
    private MemberLevelDao memberLevelDao;
    @Test
    void contextLoads() {
        MemberLevelEntity defaultLevel = memberLevelDao.getDefaultLevel();
        System.out.println(defaultLevel);
    }
}
