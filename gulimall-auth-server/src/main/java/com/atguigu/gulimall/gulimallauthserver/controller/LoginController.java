package com.atguigu.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.gulimallauthserver.feign.MemberFeignService;
import com.atguigu.gulimall.gulimallauthserver.feign.ThirdPartFeignService;
import com.atguigu.gulimall.gulimallauthserver.vo.UserLoginVo;
import com.atguigu.gulimall.gulimallauthserver.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/12 8:41
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MemberFeignService memberFeignService;
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis()-l<60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),
                        BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String senCode = UUID.randomUUID().toString().substring(0, 5);
        String substring = senCode+"_"+System.currentTimeMillis();

//       加入缓存 并且 设置缓存时间
        stringRedisTemplate.opsForValue()
                .set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,substring,10, TimeUnit.DAYS);
        //        发送验证码
        R r = thirdPartFeignService.sendCode(phone, senCode);
        return R.ok();
    }

    /**
     *  // TODO 重定向携带数据 利用session 原理 ，将数据保存在 session 中，
     *  只要跳到下一个 页面取出 数据后 session 数据 都会 删除对应的数据
     *
     *  TODO 1. 分布式下的session问题
     * @param vo
     * @param result
     * @param redirect
     * @return
     */
    @PostMapping("/register")
    public String regist(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirect){
        if(result.hasErrors()){
            Map<String ,String > error=new HashMap<String ,String>();
//            result.getFieldErrors().stream().map(fieldError->{
//                String field = fieldError.getField();
//                String defaultMessage = fieldError.getDefaultMessage();
//                error.put(field,defaultMessage);
//            })
            error=result.getFieldErrors().stream().collect(Collectors.toMap(fieldError->{
                return fieldError.getField();
            },fieldError->{
                return fieldError.getDefaultMessage();
            }));
//             这个数据 只取一次
            redirect.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
//            return "redirect:/forword:"
        }
//      正真的注册
//        1. 校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isEmpty(code)){
            if(code.equals(s.split("_")[0])){
//                删除验证码 令牌机制  TODO 暂时去掉
//                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+vo.getPhone());
//            验证码 通过 调用 远程服务
                R register = memberFeignService.register(vo);
                if(register.getCode()== 0){
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    Map<String ,String > error=new HashMap<String ,String>();
                    error.put("msg",register.getData("msg",new TypeReference<String>(){}));
                    redirect.addFlashAttribute("errors",error);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }else{
                Map<String ,String > error=new HashMap<String ,String>();
                error.put("code","验证码错误");
                redirect.addFlashAttribute("errors",error);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else{
            Map<String ,String > error=new HashMap<String ,String>();
            error.put("code","验证码错误");
            redirect.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }
    @PostMapping("/login")
    public  String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R login = memberFeignService.login(vo);
        if(login.getCode()==0){
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://gulimall.com";
        }else{
            Map<String ,String > errors=new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    @GetMapping({"/login.html","/","/index","/index.html"}) // auth
    public String loginPage(HttpSession session){
        // 从会话从获取loginUser
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);// "loginUser";
        System.out.println("attribute:"+attribute);
        if(attribute == null){
            return "login";
        }
        System.out.println("已登陆过，重定向到首页");
        return "redirect:http://gulimall.com";
    }
}
