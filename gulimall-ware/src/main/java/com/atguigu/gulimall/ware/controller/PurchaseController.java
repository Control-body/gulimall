package com.atguigu.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 采购信息
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:39:22
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 获取未领取的采购单的内容
     * http://localhost:88/api/ware/purchase/unreceive/list?t=1658928338089
     */
    @GetMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String ,Object> params) {
        PageUtils page=purchaseService.queryPageUnreceive(params);
        return R.ok().put("page",page);
    }
    /**
     * 合并菜单选项
     * /api/ware/purchase/merge
     */
    @PostMapping("/merge")
    public R unreceiveList(@RequestBody MergeVo merge) {
        purchaseService.mergePurchases(merge);
        return R.ok();
    }
/**
 * /api/ware/purchase/received
 * 给员工系统发送采购单
 */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids) {
        purchaseService.received(ids);
        return R.ok();
    }
    /**
     * /ware/purchase/done
     * 完成
     */
    @PostMapping("/done")
    public R finish(@RequestBody PurchaseDoneVo done) {
        purchaseService.done(done);
        return R.ok();
    }
}
