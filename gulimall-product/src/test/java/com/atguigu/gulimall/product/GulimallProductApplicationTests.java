package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {


    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
//        数据的增加
//        boolean save = brandService.save(brandEntity);
//        if (save){
//            System.out.println(
//                    "保存成功"
//            );
//        }
//        数据的修改
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("修改后");
//        boolean b = brandService.updateById(brandEntity);
//        if (b){
//            System.out.println(
//                    "修改成功"
//            );
//        }
        List<BrandEntity> branId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        branId.forEach((item) ->{
            System.out.println(
                    item
            );
        });
    }

}
