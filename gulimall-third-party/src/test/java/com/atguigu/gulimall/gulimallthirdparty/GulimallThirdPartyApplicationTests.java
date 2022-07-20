package com.atguigu.gulimall.gulimallthirdparty;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;

    @Test
    void contextLoads() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\luer\\Pictures\\Screenshots\\004.jpg");
        try {
//           客户端进行文件上传
            ossClient.putObject("gulimail-hellolb", "testCloud.jpg", fileInputStream);
        } catch (OSSException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }finally{
            System.out.println(
                    "文件上传成功"
            );
//            最后文件上传成功 关闭客户端
            ossClient.shutdown();
        }



    }

}
