package com.giming.GimingAPI.rpcImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.giming.GimingAPI.mapper.UserMapper;
import com.giming.GimingAPI.model.entity.User;
import com.giming.GimingAPI.service.UserService;
import com.giming.gimingapi.common.Service.CommonUserService;
import com.giming.gimingapi.common.model.UserCommon;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO
 *
 * @author GimingRao
 * @data 2023/3/27 15:00
 */
@DubboService
public class RPCUserServiceImpl implements CommonUserService {
    @Resource
    UserMapper userMapper;

    /**
     * 根据密匙获取user
     *
     * @param accessKey 访问密钥
     * @return {@code UserCommon}
     */


    @Override
    public String getSecretKeyByAccessKey(String accessKey) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey,accessKey);
        User users = userMapper.selectOne(queryWrapper);
        System.out.println("=========================");
        System.out.println("User"+users);
        System.out.println("=========================");
        return users.getSecretKey();
    }
}
