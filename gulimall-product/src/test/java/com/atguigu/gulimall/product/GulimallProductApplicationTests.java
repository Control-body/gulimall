package com.atguigu.gulimall.product;


import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {


    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    @Test
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
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
        branId.forEach((item) -> {
            System.out.println(
                    item
            );
        });
    }

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }
//    @Test
//    文件上传组件的测试用例
//    public void testUpload() throws Exception {}{
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tLqdt2oXWec2nnZUEzv";
//        String accessKeySecret = "XNaMy8k2bgtfF63eH2H34Nfa7V8lMM";
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "gulimail-hellolb";
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        String objectName = "exampledir/red.jpg";
//        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
//        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath = "C:\\Users\\luer\\Pictures\\Screenshots\\003.jpg";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        try {
//            InputStream inputStream = new FileInputStream(filePath);
//            // 创建PutObject请求。
//            ossClient.putObject(bucketName, objectName, inputStream);
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
    @Test
    public void testPathvalue(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完成路径: {}", Arrays.asList(catelogPath));
    }
}


