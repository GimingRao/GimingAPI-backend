package com.giming.GimingAPI.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giming.GimingAPI.common.ErrorCode;
import com.giming.GimingAPI.model.entity.InterfaceInfo;
import com.giming.GimingAPI.service.InterfaceInfoService;
import com.giming.GimingAPI.exception.BusinessException;
import com.giming.GimingAPI.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-03-06 18:43:03
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo==null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String method = interfaceInfo.getMethod();
        String description = interfaceInfo.getDescription();
        String requestHeader = interfaceInfo.getRequestHeader();
        String requestParams = interfaceInfo.getRequestParams();
        String responseHeader = interfaceInfo.getResponseHeader();
        if (add){
            if (StringUtils.isAnyBlank(name,method,description,requestHeader,responseHeader)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }
}




