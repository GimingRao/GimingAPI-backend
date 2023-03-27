package com.giming.gimingapi.common.Service;

import com.giming.gimingapi.common.model.UserCommon;

/**
 * 公共模块UserService
 *
 * @author GimingRao
 * @data 2023/3/27 14:51
 */
public interface CommerUserService {
    UserCommon getUserByAccessKey(int accessKey);
}
