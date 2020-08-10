package com.example.mall.vo;

import com.example.mall.enums.ResponseEnum;
import com.example.mall.pojo.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {
    //0:成功
    private  Integer status;
    private  String msg;
    private  T data;

    private ResponseVo(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ResponseVo(Integer status,T data) {
        this.status = status;
        this.data=data;
    }

    public  static <T> ResponseVo<T> successByMsg(String msg){
        return  new ResponseVo<T>(ResponseEnum.SUCCESS.getCode(),msg);
    }
    public  static <T> ResponseVo<T> error(ResponseEnum responseEnum){
        return  new ResponseVo<T>(responseEnum.getCode(),responseEnum.getMsg());
    }
    public  static <T> ResponseVo<T> error(ResponseEnum responseEnum,String msg){
        return  new ResponseVo<T>(responseEnum.getCode(),msg);
    }
    public  static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult result){
        return  new ResponseVo<T>(responseEnum.getCode(),
                Objects.requireNonNull(result.getFieldError()).getField()
                +"   "+
                result.getFieldError().getDefaultMessage());
    }

    public static <T>ResponseVo<T> success() {
        return new ResponseVo<T>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMsg());
    }

    public static <T>ResponseVo<T> success(T t) {
        return  new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),t);
    }
}
