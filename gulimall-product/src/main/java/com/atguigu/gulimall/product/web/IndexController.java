package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/1 6:43
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @RequestMapping({"/","/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntitys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEntitys);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> map=categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public  String hello(){
//        创建锁
        RLock lock = redissonClient.getLock("my_lock");
//        锁操作
        lock.lock();
        try{
            System.out.println("加锁成功！！！！执行业务"+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally{
            System.out.println("施放锁--------》"+Thread.currentThread().getId());
//           解锁操作
            lock.unlock();
        }
        return "hello";
    }
    @ResponseBody
    @GetMapping("/write")
    public  String write(){
        String s = null;
        RReadWriteLock anyRWLock = redissonClient.getReadWriteLock("anyRWLock");
        RLock rLock = anyRWLock.writeLock();
        try {
            rLock.lock();
            System.out.println("写锁加入成功"+Thread.currentThread().getId());
            s=UUID.randomUUID().toString();
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("valueRead",s);
        }catch (Exception e){

        }finally{
//            释放锁
            rLock.unlock();
            System.out.println("写锁释放成功！！！"+Thread.currentThread().getId());
        }
        return s;
    }
    @ResponseBody
    @GetMapping("/read")
    public  String read(){
        RReadWriteLock anyRWLock = redissonClient.getReadWriteLock("anyRWLock");
        RLock rLock = anyRWLock.readLock();
        String valueRead=null;
    try {
          rLock.lock();
          valueRead = (String) stringRedisTemplate.opsForValue().get("valueRead");
        Thread.sleep(30000);
        }catch (Exception e){

        }finally {
        rLock.unlock();
        }
        return valueRead;
    }
    @ResponseBody
    @GetMapping("/park")
    public  String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
//      获取信号量  阻塞时等待 必须等待上 否则不通过
//        park.acquire();;
//        非阻塞式的 当量小于零的时候直接 返回false
        boolean b = park.tryAcquire(2);
        return  "挺进了2";
    }

    @ResponseBody
    @GetMapping("/go")
    public  String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
//      获取释放信号
        park.release();;
        return  "go";
    }
/**
 * 放假锁门
 */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lookDoor() throws InterruptedException {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        latch.trySetCount(1);
        latch.await();
        return "放假了";
    }
    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogog(@PathVariable("id") long id) throws InterruptedException {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        latch.countDown();
        return id+"班的都走了";
    }

}
