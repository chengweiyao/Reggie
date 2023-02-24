package com.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})//捕获全局异常的
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){ //异常会有这个字段Duplicate entry '李嘉琪' for key 'idx_username'
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";//Duplicate entry '李嘉琪' for key 'idx_username' 第二个
            return R.error(msg);
        }
        return R.error("失败了");
    }


    //捕获异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());//给出错误

        return R.error(ex.getMessage());
    }





}
