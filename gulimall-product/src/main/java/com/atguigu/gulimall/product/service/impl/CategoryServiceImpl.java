package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog3Vo;
import com.atguigu.gulimall.product.vo.Catelog2Vo;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
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
     * 以此查找对应的路径
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        ArrayList<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);
        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 更新自己和其他关联的数据
     * @param category
     * allEntries 删除这个 category 分片的所有数据
     *
     */
    @Caching(
            evict={
                    @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
                    @CacheEvict(value = {"category"},key = "'getCatelogJson'")
            }
    )
//    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
//        更新自己
        this.updateById(category);
//        更新关联表中的
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }

    @Cacheable(value = "category",key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 得到以及分类的菜单
     * 使用注解的方式进行写入
     *
     */
    @Cacheable(value = "category" ,key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        查出所有的以及分类
        List<CategoryEntity> level1Categorys =  getParent_cid(categoryEntities,0L);
        ;

        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
//                    每一个分类的二级分类
                    List<CategoryEntity> categoryEntitys = getParent_cid(categoryEntities,v.getCatId());
                    List<Catelog2Vo> collect = null;
                    if (categoryEntitys != null) {
                        collect = categoryEntitys.stream().map(le2 -> {
//                      根据二级分类的Id 构造三级分类

                            List<CategoryEntity> level3 = getParent_cid(categoryEntities,le2.getCatId());
                            List<Catalog3Vo> collect3=null;
                            if(level3!=null){
                                collect3= level3.stream().map(le3 -> {
//                                    封装数据
                                    Catalog3Vo catalog3Vo = new Catalog3Vo(le3.getCatId().toString(),le3.getName(),le2.getCatId().toString());

                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                            }
                            Catelog2Vo catelog2Vo = new Catelog2Vo(le2.getCatId().toString(), le2.getName(), v.getCatId().toString(), collect3);
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return collect;
                }
        ));
        return parent_cid;
    }
    /**
     * 查询数据库 的分类星系
     * 添加缓存   放入的值 都是 String 字符串
     * json 的数据 就比较通用
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String category = stringStringValueOperations.get("catelogJSON");
        if(StringUtils.isEmpty(category)){
//            如果在缓存中拆查询不到值 就在redis 中进行添加值
            Map<String, List<Catelog2Vo>> catelogJsonFromDB = getCatelogJsonFromDBWithRedisLock();
            String s1 = JSON.toJSONString(catelogJsonFromDB);
            stringRedisTemplate.opsForValue().set("catelogJSON",s1);
            return catelogJsonFromDB;
        }
        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(category, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return stringListMap;
    }



    private Map<String, List<Catelog2Vo>> getCatelogJsonFromDB() {
        String category = stringRedisTemplate.opsForValue().get("catelogJSON");
        if(!StringUtils.isEmpty(category)){
            System.out.println("缓存命中 --------返回");
//            如果在缓存中拆查询不到值 就在redis 中进行添加值
            Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(category, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return stringListMap;
        }
        System.out.println("缓存不命中 --------查询数据库");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        查出所有的以及分类
        List<CategoryEntity> level1Categorys =  getParent_cid(categoryEntities,0L);
        ;

        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
//                    每一个分类的二级分类
                    List<CategoryEntity> categoryEntitys = getParent_cid(categoryEntities,v.getCatId());
                    List<Catelog2Vo> collect = null;
                    if (categoryEntitys != null) {
                        collect = categoryEntitys.stream().map(le2 -> {
//                      根据二级分类的Id 构造三级分类

                            List<CategoryEntity> level3 = getParent_cid(categoryEntities,le2.getCatId());
                            List<Catalog3Vo> collect3=null;
                            if(level3!=null){
                                collect3= level3.stream().map(le3 -> {
//                                    封装数据
                                    Catalog3Vo catalog3Vo = new Catalog3Vo(le3.getCatId().toString(),le3.getName(),le2.getCatId().toString());

                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                            }
                            Catelog2Vo catelog2Vo = new Catelog2Vo(le2.getCatId().toString(), le2.getName(), v.getCatId().toString(), collect3);
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return collect;
                }
        ));

//           将数据放入缓存
        String s1 = JSON.toJSONString(parent_cid);
        stringRedisTemplate.opsForValue().set("catelogJSON",s1);
        return parent_cid;
    }

    /**
     * 使用分布式锁的方式：
     * 1. 原则性加锁
     * 2. 原子性解锁
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedissionLock() {
//        增加reddison 锁的名字 所得粒度 越细 越快
        RLock lock = redisson.getLock("catelogJson-lock");
        lock.lock();

            Map<String, List<Catelog2Vo>> catelogJsonFromDB;
//            加锁成功  查询数据库
            try{
                catelogJsonFromDB= getCatelogJsonFromDB();
            }finally{
//                reddissn 进行解锁
                lock.unlock();
            }
            return catelogJsonFromDB;

    }
    /**
     * 使用分布式锁的方式：
     * 1. 原则性加锁
     * 2. 原子性解锁
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
//         redis 占坑操作  加锁 --》原则性操作
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,300, TimeUnit.SECONDS);
        if (lock){
            System.out.println("获取分布式锁成功！！！！");
            Map<String, List<Catelog2Vo>> catelogJsonFromDB;
//            加锁成功  查询数据库
            try{
                catelogJsonFromDB= getCatelogJsonFromDB();
            }finally{
                // 删除也必须是原子操作 Lua脚本操作 删除成功返回1 否则返回0
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                // 原子删锁
                stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return catelogJsonFromDB;
        }else{
            System.out.println("获取分布式锁-----》失败！！！！");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            加锁失败 重新验证
            return getCatelogJsonFromDBWithRedisLock();// 自旋的方式

        }
    }
    /**
     * 从数据库查询数据 --》 性能地下
     * 得到所有的分类信息
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithLocalLock() {
//        TODO 本地锁 synchronized JUC（lock） 在分布式情况下 想要锁祝所有 必须使用分布式锁
        synchronized (this){
//            SpringBoot 的bean 都是单例的， 只要是这个对象 都会锁住,但是只能 锁住本地线程
            //       预先查询数据库 先进行保存 然后 在根据ID 到这查；
//            双重验证
            String category = stringRedisTemplate.opsForValue().get("catelogJSON");
            if(!StringUtils.isEmpty(category)){
                System.out.println("缓存命中 --------返回");
//            如果在缓存中拆查询不到值 就在redis 中进行添加值
                Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(category, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return stringListMap;
            }
            System.out.println("缓存不命中 --------查询数据库");
            List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        查出所有的以及分类
            List<CategoryEntity> level1Categorys =  getParent_cid(categoryEntities,0L);;

            Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                    k -> k.getCatId().toString(),
                    v -> {
//                    每一个分类的二级分类
                        List<CategoryEntity> categoryEntitys = getParent_cid(categoryEntities,v.getCatId());
                        List<Catelog2Vo> collect = null;
                        if (categoryEntitys != null) {
                            collect = categoryEntitys.stream().map(le2 -> {
//                      根据二级分类的Id 构造三级分类

                                List<CategoryEntity> level3 = getParent_cid(categoryEntities,le2.getCatId());
                                List<Catalog3Vo> collect3=null;
                                if(level3!=null){
                                    collect3= level3.stream().map(le3 -> {
//                                    封装数据
                                        Catalog3Vo catalog3Vo = new Catalog3Vo(le3.getCatId().toString(),le3.getName(),le2.getCatId().toString());

                                        return catalog3Vo;
                                    }).collect(Collectors.toList());
                                }
                                Catelog2Vo catelog2Vo = new Catelog2Vo(le2.getCatId().toString(), le2.getName(), v.getCatId().toString(), collect3);
                                return catelog2Vo;
                            }).collect(Collectors.toList());
                        }
                        return collect;
                    }
            ));

//           将数据放入缓存
            String s1 = JSON.toJSONString(parent_cid);
            stringRedisTemplate.opsForValue().set("catelogJSON",s1);
            return parent_cid;

        }
    }

    /**
     * 根据 一次查询的 list 查询 对应的实体
     * @param selectList
     * @param parent_cid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catelogId, ArrayList<Long> path){
        path.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
           findParentPath(byId.getParentCid(),path);
        }
        return path;
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