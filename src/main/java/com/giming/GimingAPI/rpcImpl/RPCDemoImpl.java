package com.giming.GimingAPI.rpcImpl;




import com.giming.gimingapi.common.Service.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

/**
 * TODO
 *
 * @author GimingRao
 * @data 2023/3/27 10:30
 */
@DubboService
public class RPCDemoImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        System.out.println("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name;
    }
}
