package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import javax.annotation.PostConstruct;


/**
 * 属性分组
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:58:14
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrService attrService;
    @Autowired
    AttrAttrgroupRelationService relationService;
    /**
     * 新增关联关系接口
     * /product/attrgroup/attr/relation
     */
    @PostMapping("/attr/relation")
    public  R addRelation(@RequestBody List<AttrGroupRelationVo> attrgroups){
        relationService.saveBatch(attrgroups);
        return R.ok();
    }
    /**
 * : http://localhost:88/api/product/attrgroup/1/noattr/relation
 * 查询本分组 未被进行关联的属性
 */
     @GetMapping("{attrgroupId}/noattr/relation")
     public R AttrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                             @RequestParam Map<String,Object> params) {
         PageUtils page=attrService.getNoRelationAttr(params,attrgroupId);
         return R.ok().put("page",page);
     }
    /**
     * http://localhost:88/api/product/attrgroup/1/attr/relation?t=1658715968217
     * 查询分组对应的属性
     */
    @GetMapping("/{attrgroupId}/attr/relation")
     public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
//        根据分组的Id 找出对应的分组信息
        List<AttrEntity> entity=attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entity);
     }
    /**
     * http://localhost:88/api/product/attrgroup/attr/relation/delete
     *
     * 移除对应的分组信息
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrGroupService.deleteRelation(vos);
        return R.ok();
    }
/**
 * http://localhost:88/api/product/attrgroup/225/withattr?t=1658829374014
 * 查询规格参数的 分组 和对应的值
 */
     @GetMapping("/{catelogId}/withattr")
     public R getAttrGroupWithAttributes(@PathVariable("catelogId") Long catelogId){
         // 查询这个分类下的所有分组

         // 查询这个分组下所i有属性
         List<AttrGroupWithAttrsVo> vos=  attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
         return R.ok().put("data",vos);
     }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path=categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
