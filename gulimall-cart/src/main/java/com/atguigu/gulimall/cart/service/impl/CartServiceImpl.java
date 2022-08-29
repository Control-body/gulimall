package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/22 19:31
 *
 * @author Control.
 * @since JDK 1.8
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
//注入 远程 依赖
    @Autowired
    ProductFeignService productFeignService;
//    注入 线程池
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "ATGUIGU:cart:";

    /**
     * 添加到 购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
//       获取对应 的 购物车
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)){
//           没有商品 需要添加商品
            CartItem cartItem = new CartItem();
//        1. 远程 查询 商品的 信息
            CompletableFuture<Void> skuInfo = CompletableFuture.runAsync(() -> {

                R r = productFeignService.SkuInfo(skuId);
                SkuInfoVo sku = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                // 2. 填充购物项
                cartItem.setCount(num);
                cartItem.setCheck(true);
                cartItem.setImage(sku.getSkuDefaultImg());
                cartItem.setPrice(sku.getPrice());
                cartItem.setTitle(sku.getSkuTitle());
                cartItem.setSkuId(skuId);
            }, executor);
//       远程 查询 sku 的 组合 信息
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(skuSaleAttrValues);
            }, executor);
            // 当两个 都 查询完成后 在执行下面的 操作
            CompletableFuture.allOf(skuInfo,voidCompletableFuture).get();

            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),s);
            return cartItem;
        }else {
            CartItem cartItem = new CartItem();
//            当有商品 修改数量 等信息
            cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
//            跟新 redis
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }
    }



    /**
     * 获取整个 购物车信息
     * @return
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        //       用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()!=null){
//            已经登录了  还要 合并操作
            String cartId = CART_PREFIX+userInfoTo.getUserId();
//看临时的购物车中 有没有有数据
            String cartKey = CART_PREFIX+userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            if(cartItems!=null){
//                临时购物车 有商品  合并 商品
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
//                清除 临时的商品
                clearCart(cartKey);
            }
//                获取登录后的 商品信息   [包含 临时登录 数据 和 登录后的 购物车 数据]
            List<CartItem> cartItems1 = getCartItems(cartId);
            cart.setItems(cartItems1);
        }else {
//            没有登录
            String cartKey = CART_PREFIX+userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 清除 临时购物车数据
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey) {
       redisTemplate.delete(cartKey);

    }

    /**
     *  修改 指定商品的 状态
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    /**
     * 删除 购物车 数据
     * @param skuId
     */
    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 返回对应 用户 绑定的 购物车 Hashmap
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //       用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String  cartKey="";
//        判断 是否登录 还是 临时用户
        if(userInfoTo.getUserId()!=null){
//            登录了
            cartKey=CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey=CART_PREFIX+userInfoTo.getUserKey();
        }
//        操作 购物车  绑定了 这个 redis 的操作
        BoundHashOperations<String, Object, Object> hashKey =
                redisTemplate.boundHashOps(cartKey);
        return  hashKey;
    }

    /**
     * 获取 指定 购物车的 信息
     */
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashKey = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashKey.values();
        if(values!=null && values.size()>0){
            List<CartItem> collect = values.stream().map((obj) -> {
                String str= (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
    /**
     * 从 redis 中 进行查询 返回指定 购物车 指定的 商品
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(res, CartItem.class);
        return cartItem;
    }
}
