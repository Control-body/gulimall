package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品属性
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:58:14
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * /api/product/attr/base/listforspu/4
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R baseListforspu(@PathVariable("spuId") Long params){
        List<ProductAttrValueEntity> entities=productAttrValueService.baseListforspu(params);
        return R.ok().put("data",entities);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 http://localhost:88/api/product/attr/info/3
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo AttrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", AttrRespVo);
    }

    /**
     * 保存
     * 使用Vo实体类进行 封装
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
//
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改  http://localhost:88/api/product/attr/update
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
//		attrService.updateById(attr);
        attrService.updateAttr(attr);

        return R.ok();
    }
    /**
     * 批量进行修改数据
     *  http://localhost:88/api/product/attr/update/3
     */
    @PostMapping("/update/{spuId}")
    public R update(@PathVariable Long spuId,@RequestBody List<ProductAttrValueEntity> entities){

        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }
    /**
     * api/product/attr/base/list/0?t=116&page=1&limit=10&key=
     *
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type){
//       根据参数分页查询 条件查询
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId,type);
        return R.ok().put("page",page);
    }


}
