package com.giming.GimingAPI.rpc.impl;

import com.giming.GimingAPI.rpc.RPCDemoSerivce;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

/**
 * TODO
 *
 * @author GimingRao
 * @data 2023/3/27 10:30
 */
@DubboService
public class RPCDemoImpl implements RPCDemoSerivce {
    @Override
    public String sayHello(String name) {
        System.out.println("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name;
    }
}
