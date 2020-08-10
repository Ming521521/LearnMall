package com.example.mall.exception;

import com.example.mall.enums.ResponseEnum;
import com.example.mall.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo notValidate(MethodArgumentNotValidException e){
        BindingResult bindingResult=e.getBindingResult();
        return  ResponseVo.error(ResponseEnum.PARAM_ERROR,bindingResult);
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
//    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo handle(RuntimeException e){
        return  ResponseVo.error(ResponseEnum.ERROR,e.getMessage());
    }

    @ExceptionHandler(UserLoginException.class)
    @ResponseBody
    public ResponseVo handleUserLogin(UserLoginException e){
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }
}
