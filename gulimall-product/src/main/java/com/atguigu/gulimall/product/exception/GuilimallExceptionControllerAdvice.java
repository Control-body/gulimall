package com.atguigu.gulimall.product.exception;

import com.atguigu.common.utils.R;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.atguigu.common.exception.BizCodeEnum.UNKNOWN_EXCEPTION;
import static com.atguigu.common.exception.BizCodeEnum.VAILD_EXCEPTION;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/7/21 21:02
 *
 * @author Control.
 * @since JDK 1.8
 */

/**
 * 集中处理所有的异常
 */
@Slf4j
@ResponseBody
@ControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GuilimallExceptionControllerAdvice {

    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据出现问题{},异常类型{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((filedError)->{
            errorMap.put(filedError.getField(),filedError.getDefaultMessage());
        });
        return R.error(VAILD_EXCEPTION.getCode(),VAILD_EXCEPTION.getMsg()).put("data",errorMap);
    }


    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        log.error("错误",e);

        return R.error(UNKNOWN_EXCEPTION.getCode(),UNKNOWN_EXCEPTION.getMsg());
    }
}
