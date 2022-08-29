package com.atguigu.gulimall.gulimallthirdparty;

import com.atguigu.gulimall.gulimallthirdparty.component.SmsComponent;
import com.atguigu.gulimall.gulimallthirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/12 9:25
 *
 * @author Control.
 * @since JDK 1.8
 */

@SpringBootTest
public class SsmTest {

    @Test
    public void sendSsm(){
            String host = "https://dfsns.market.alicloudapi.com";
            String path = "/data/send_sms";
            String method = "POST";
            String appcode = "eeaa920cd69e46daaa4f009e0498099e";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            //根据API的要求，定义相对应的Content-Type
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Map<String, String> querys = new HashMap<String, String>();
            Map<String, String> bodys = new HashMap<String, String>();
            bodys.put("content", "code:1234");
            bodys.put("phone_number", "18730121587");
            bodys.put("template_id", "TPL_0000");


            try {
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Autowired
    SmsComponent smsComponent;
    @Test
    public void Test(){
        smsComponent.sendSmsCode("18730121587","1112");
    }

}
