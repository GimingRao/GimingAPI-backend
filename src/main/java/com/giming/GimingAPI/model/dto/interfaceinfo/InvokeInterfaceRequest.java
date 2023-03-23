package com.giming.GimingAPI.model.dto.interfaceinfo;

import lombok.Data;

/**
 * TODO
 *
 * @author GimingRao
 * @data 2023/3/23 13:37
 */
@Data
public class InvokeInterfaceRequest {
    /**
     * 请求接口id
     */
    private Long id;

    /**
     * 请求参数
     */
    private String requestParams;
}
