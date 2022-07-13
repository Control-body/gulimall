package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
//       查询所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//      2. 组装成父子结构的树性结构与
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter((categoryEntitie) ->
             categoryEntitie.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu,categoryEntities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    /**
     * 批量删除 父类的下面的所有的 ID数
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
//        todo 检查菜单是否被其他地方引用

        baseMapper.deleteBatchIds(asList);// 进行批量的删除
    }

    /**
     * 递归查早 对应的子菜单
     * @param root
     * @param all
     * @return
     */
    public List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter(
                        (categoryEntitie) -> {
                            return categoryEntitie.getParentCid() == root.getCatId();
                        }
                ).map(categoryEntitie -> {
                    categoryEntitie.setChildren(getChildrens(categoryEntitie, all));
                    return categoryEntitie;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }


}