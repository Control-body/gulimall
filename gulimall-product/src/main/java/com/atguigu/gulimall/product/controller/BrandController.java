package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:58:14
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }
    @GetMapping("/infos")
    public  R info(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brands=brandService.getBrandsByIds(brandIds);
        return R.ok().put("brands",brands);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * BindingResult 在校验数据的后面紧跟着 就可以获得到 校验数据的校验信息
     */
    @RequestMapping("/save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand/*, BindingResult result*/){
//        使用全局异常处理类进行异常处理
//        if(result.hasErrors()){
//            HashMap<String, String> map = new HashMap<>();
////             如果没有问题才会继续执行
//            result.getFieldErrors().forEach((item)->{
////                获取错误的信息
//                String defaultMessage = item.getDefaultMessage();
////                获取错误的字段
//                String field = item.getField();
//                map.put(field,defaultMessage);
//            });
//            return R.error(400," 提交的数据不合法").put("data",map);
//        }else{
//
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
//        不仅仅是跟新一个表 把关联的表都做修改
		brandService.updateDetail(brand);
        return R.ok();
    }
    @RequestMapping("/updateStatus")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
