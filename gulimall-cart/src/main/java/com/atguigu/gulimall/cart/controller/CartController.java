package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/22 19:35
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class CartController {
    @Autowired
    CartService cartService;
    private final String RedirectPATH = "redirect:http://cart.gulimall.com/cart.html";
          /**
         * 修改 购物车属性
         *http://cart.gulimall.com/checkItem.html?skuId=12&check=0
         */
          @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam(value="skuId" )Long skuId,
                            @RequestParam(value="check")Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.gulimall.com/cart.html";
    }
    /**
     * 跳转 购物车页面
     * 浏览器有一个cookie：user-key 标识用户身份 一个月后过期
     * 	 * 每次访问都会带上这个 user-key
     * 	 * 第一次 如果没有临时用户 还要帮忙创建一个
     *
     *
     *
     * 	 如果登录 ： session中有 ，如果没有登录 就帮忙 创建一个
     * @return
     */
    @GetMapping({"/","/cart.html"})
    public String carListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart=cartService.getCart();
        model.addAttribute("cart",cart);
        return  "cartList";
    }

    /**
     * 添加商品 到 购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String  addToCart(@RequestParam("skuId") Long  skuId,
                             @RequestParam("num") Integer num,
                             RedirectAttributes ra) throws ExecutionException, InterruptedException {
        CartItem cartItem=cartService.addToCart(skuId,num);
//        model.addAttribute("item",cartItem);
//        ra.addFlashAttribute(skuId);
        ra.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam(value = "skuId",required = false) Long skuId,
                                       Model model)
            throws ExecutionException{
//        从redis 中 进行 查询，  重定向 到成功的 页面
        CartItem cartItem=cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

    /**
     * 修改 购物车的 数量
     * @return
     */
    @GetMapping("/countItem")
    public  String  countItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);
        return RedirectPATH;
    }

    /**
     * 删除 购物车数据
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return RedirectPATH;
    }

}
