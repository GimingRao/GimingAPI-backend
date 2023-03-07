package com.giming.GimingAPI.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giming.GimingAPI.model.entity.InterfaceInfo;

/**
* @author admin
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-03-06 18:43:03
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
