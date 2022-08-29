package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.vo.ItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/10 17:54
 *
 * @author Control.
 * @since JDK 1.8
 */
@Slf4j
@SpringBootTest
public class MysqlTest {
    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test
    public  void test (){
        List<SpuItemAttrGroup> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(6L, 225L);
        for (SpuItemAttrGroup spuItemAttrGroup : attrGroupWithAttrsBySpuId) {
            System.out.println(spuItemAttrGroup);
        }
    }
    @Test
    public  void test2(){
        List<ItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(6L);
        for (ItemSaleAttrVo itemSaleAttrVo : saleAttrsBySpuId) {
            System.out.println(itemSaleAttrVo);
        }
    }
}
