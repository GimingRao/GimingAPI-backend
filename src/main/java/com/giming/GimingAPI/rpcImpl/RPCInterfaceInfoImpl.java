package com.giming.GimingAPI.rpcImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.giming.GimingAPI.mapper.InterfaceInfoMapper;
import com.giming.GimingAPI.model.entity.InterfaceInfo;
import com.giming.gimingapi.common.Service.CommonInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * RPC中InterfaceInfo的service
 *
 * @author GimingRao
 * @data 2023/3/27 15:49
 */
@DubboService
public class RPCInterfaceInfoImpl implements CommonInterfaceInfoService {
    @Resource
    InterfaceInfoMapper interfaceInfoMapper;
    /**
     * 接口是否存在
     *
     * @param url    url
     * @param method 方法
     * @return boolean
     *
     * 查询url与方法存在的InterfaceInfo，验证数量
     */

    @Override
    public boolean isExistInterface(String url, String method) {
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getUrl , url)
                .eq(InterfaceInfo::getMethod , method);
        return interfaceInfoMapper.selectCount(queryWrapper) == 1;
    }
}
