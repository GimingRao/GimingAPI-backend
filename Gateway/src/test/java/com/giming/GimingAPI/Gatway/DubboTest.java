package com.giming.GimingAPI.Gatway;


import com.giming.gimingapi.common.Service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TODO
 *
 * @author GimingRao
 * @data 2023/3/27 13:42
 */
@SpringBootTest
public class DubboTest {
    @DubboReference
    private DemoService rpcDemoService;

    @Test
    void testRpc() {
        System.out.println(rpcDemoService.sayHello("world"));
    }
}
