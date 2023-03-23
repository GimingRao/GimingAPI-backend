package com.giming.GimingAPI.controller;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giming.GimingAPI.common.*;
import com.giming.GimingAPI.model.dto.interfaceinfo.InvokeInterfaceRequest;
import com.google.gson.Gson;
import com.giming.GimingAPI.annotation.AuthCheck;
import com.giming.GimingAPI.constant.CommonConstant;
import com.giming.GimingAPI.constant.UserConstant;
import com.giming.GimingAPI.exception.BusinessException;
import com.giming.GimingAPI.exception.ThrowUtils;
import com.giming.GimingAPI.model.dto.interfaceinfo.InterfaceInfoAddRequest;

import com.giming.GimingAPI.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.giming.GimingAPI.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.giming.GimingAPI.model.entity.InterfaceInfo;
import com.giming.GimingAPI.model.entity.User;
import com.giming.GimingAPI.service.InterfaceInfoService;
import com.giming.GimingAPI.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/GimingRao">Giming</a>
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);

        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);

    }

    /**
     * 上线接口
     *仅管理员可用
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IDRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo interfaceInfo = this.interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //判断接口是否可用
        //todo
        //
        //更改接口状态
        interfaceInfo.setStatus(1);
        boolean b = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(b);
    }
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IDRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo interfaceInfo = this.interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //更改接口状态
        interfaceInfo.setStatus(0);
        boolean b = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(b);
    }


    /**
     * 调用接口信息
     *
     * @param invokeInterfaceRequest 调用接口信息
     * @param request                http请求
     * @return {@code BaseResponse<Boolean>}
     * 1. 健壮性检验
     * 2. 得到ak、sk
     * 3. 查库得到url与请求方式
     * 4. 生成密匙
     * 5. HuTuHttp发送请求
     */
    @PostMapping("/innvoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InvokeInterfaceRequest invokeInterfaceRequest, HttpServletRequest request) {
        if (invokeInterfaceRequest==null||invokeInterfaceRequest.getId()<0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请求id错误");
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        //查库得到url与请求方式
        String requestParams = invokeInterfaceRequest.getRequestParams();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(invokeInterfaceRequest.getId());
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        //生成密匙
        //获取时间戳、生成nonce
        String currentTime = String.valueOf(System.currentTimeMillis());
        //secretKey+currentTime+PrioiText+accessKey加密
        String signedSecretKey = MD5.create().digestHex(secretKey+currentTime+accessKey);
        //封装成Header
        Map<String,String> head=new HashMap<>();
        head.put("signedsecretKey",signedSecretKey);
        head.put("accessKey",accessKey);
        head.put("currentTime",currentTime);
        if ("POST".equals(method)){
            String responseBody = HttpRequest.post(url)
                    .addHeaders(head)
                    .body(requestParams)
                    .execute().body();
            return ResultUtils.success(responseBody);
        }else if ("GET".equals(method)){
            JSONObject entries = JSONUtil.parseObj(requestParams);
            String urlWithParam = url + "?" + HttpUtil.toParams(entries);
            String requestBody = HttpRequest.get(urlWithParam)
                    .addHeaders(head)
                    .execute().body();
            return ResultUtils.success(request);
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方式错误");
        }
    }
}
