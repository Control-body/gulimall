package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/22 19:42
 *
 * @author Control.
 * @since JDK 1.8
 *//**
 * <p>Title: CartInterceptor</p>
 * Description：在执行目标之前 判断用户是否登录,并封装
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    // 存放当前线程用户信息
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 方法 执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
//        进行 对请求 进行拦截 包装
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
//                    设置 userkey
                    userInfoTo.setUserKey(value);
//                   表示有 用户的信息
                    userInfoTo.setTempUser(true);
                }
            }
        }
        if(attribute!=null){
//            登录了
            userInfoTo.setUserId(attribute.getId());

        }
//      如果没有 临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserKey(s);
        }
//         放入 用户的 信息
        threadLocal.set(userInfoTo);

        return true;
    }

    /**
     *  业务执行 之后
     *  分配 临时 用户，方便之后 保存数据
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {


        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.isTempUser()){
//            没有 临时用户的 信息，为 Cookie 增加 cookie
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);
            response.addCookie(cookie);
        }

    }
}
