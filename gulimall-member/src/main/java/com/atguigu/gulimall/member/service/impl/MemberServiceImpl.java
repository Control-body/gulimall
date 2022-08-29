package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.util.HttpUtils;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.atguigu.gulimall.member.vo.UserRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberDao memberDao = this.baseMapper;
//        设置默认的等级
        MemberLevelEntity memberLevel=memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevel.getId());
//        手机号  要保证唯一性 由于方法 是返回的 Void 这里使用 异常机制捕获 进行抛出
        checkPhoneUnique(vo.getPhone());
        checkUserUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());

//  保存 密码 进行 加密进行保存
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
//       保存 用户
        memberDao.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        MemberDao baseMapper = this.baseMapper;
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>0){
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUserUnique(String userName) throws UserNameExistException{
        MemberDao baseMapper = this.baseMapper;
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
//        密码进行对比
//        1. 去数据库进行查询
        // 去数据库查询
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(entity== null){
//            登录失败
            return null;
        }else {
//            进行对比
            String password1 = entity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, password1);
            if (matches){
                return entity;
            }else {
                return null;

            }
        }
    }

    /**
     * 社交登录
     * @param vo
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {

//        登录 和 注册 合并 逻辑
        String uid = socialUser.getUid();
        MemberDao baseMapper = this.baseMapper;
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (entity !=null ) {
//           用户已经注册了 跟新时间
            MemberEntity memberEntity = new MemberEntity();
            // 更新令牌
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            // 更新
            baseMapper.updateById(memberEntity);
            entity.setAccessToken(socialUser.getAccessToken());
            entity.setExpiresIn(socialUser.getExpiresIn());
            entity.setPassword(null);
            return entity;
        }else{
            // 没有注册过
            // 2. 没有查到当前社交用户对应的记录 我们就需要注册一个
            MemberEntity redirect = new MemberEntity();
            try {
                HashMap<String, String> query = new HashMap<String, String>();
                query.put("access_token", socialUser.getAccessToken());
                query.put("uid", socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if(response.getStatusLine().getStatusCode()== 200){
//                查询成功
                    String s = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(s);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");

                    redirect.setNickname(name);
                    redirect.setUsername(name);
                    redirect.setGender("m".equals(gender)?1:0);

                }
            }catch(Exception e){

            }
            redirect.setAccessToken(socialUser.getAccessToken());
            redirect.setSocialUid(socialUser.getUid());
            redirect.setExpiresIn(socialUser.getExpiresIn());
            baseMapper.insert(redirect);
            return redirect;
        }

    }

}