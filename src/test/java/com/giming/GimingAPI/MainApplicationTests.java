package com.giming.GimingAPI;

import com.example.apinlp.APINLPConfig;
import com.example.apinlp.Client.WordClient;
import com.giming.GimingAPI.config.WxOpenConfig;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/GimingRao">Giming</a>
 * *
 */
@SpringBootTest
class MainApplicationTests {
    @Resource
    WordClient wordClient;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Test
    void contextLoads() {
        System.out.println(wordClient.Reword("安徽大学"));
    }

}
