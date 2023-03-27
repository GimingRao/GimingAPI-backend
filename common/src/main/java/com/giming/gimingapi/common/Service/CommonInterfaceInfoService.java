package com.giming.gimingapi.common.Service;

/**
 * 公共模块UserService
 *
 * @author GimingRao
 * @data 2023/3/27 14:56
 */
public interface CommonInterfaceInfoService {
    /**
     * 接口是否合法的校验
     *
     * @return boolean
     */
    boolean isExistInterface (String url ,String method);
}
