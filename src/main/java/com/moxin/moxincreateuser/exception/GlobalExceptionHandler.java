package com.moxin.moxincreateuser.exception;

import com.moxin.moxincreateuser.common.BaseResponse;
import com.moxin.moxincreateuser.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
//@Hidden
@RestControllerAdvice // 该注解表示该类是一个全局异常处理器
@Slf4j
public class GlobalExceptionHandler {

    // 处理自定义的业务异常
    // 该注解表示该方法是处理BusinessException异常的
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}