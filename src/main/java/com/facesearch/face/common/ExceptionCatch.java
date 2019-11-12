package com.tt.face.common;


import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.google.common.collect.ImmutableMap;

/**
 * 统一异常捕获类
 * @author yangbs
 * @version 1.0
 * @create 
 **/
@RestControllerAdvice//控制器增强
public class ExceptionCatch {

    private static Logger logger = LoggerFactory.getLogger(ExceptionCatch.class);


    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    //捕获CustomException此类异常
    @ExceptionHandler(TTFaceException.class)
    @ResponseBody
    public ResponseResult handleCustomException(TTFaceException ttFaceException){
        //记录日志
    	logger.error("catch exception:{}",ttFaceException.getMessage());
        ResultCode resultCode = ttFaceException.getResultCode();
        return new ResponseResult(resultCode);
    }
    
  //捕获Exception此类异常
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ResponseResult handleMaxUploadSizeExceededException(MultipartException multipartException){
        //记录日志
    	logger.error("catch exception:{}",multipartException.getMessage());
        if(EXCEPTIONS == null){
            EXCEPTIONS = builder.build();//EXCEPTIONS构建成功
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(multipartException.getClass());
    	logger.error("catch exception: resultCode {}",resultCode + " class:" + multipartException.getCause().getCause());
        
        if(resultCode !=null){
            return new ResponseResult(resultCode);
        }
        if(multipartException.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException)
        	return new ResponseResult(EXCEPTIONS.get(FileUploadBase.FileSizeLimitExceededException.class));
        else if(multipartException.getCause().getCause() instanceof FileUploadBase.SizeLimitExceededException)
    		return new ResponseResult(EXCEPTIONS.get(FileUploadBase.SizeLimitExceededException.class));
        else
            return new ResponseResult(CommonCode.SERVER_ERROR);
    }
    

    
    //捕获Exception此类异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult handleException(Exception exception){
        //记录日志
    	logger.error("catch exception:{}",exception.getMessage());
        if(EXCEPTIONS == null){
            EXCEPTIONS = builder.build();//EXCEPTIONS构建成功
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        
    	logger.error("catch exception: resultCode {}",resultCode);

        if(resultCode !=null){
            return new ResponseResult(resultCode);
        }else{
        	logger.error("in SERVER_ERROR catch exception: ",exception.getClass().getName());

            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }


    }

    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
        builder.put(FileUploadBase.FileSizeLimitExceededException.class,CommonCode.FACE_UPLOAD_LIMIT_FAIL);
        builder.put(FileUploadBase.SizeLimitExceededException.class,CommonCode.FACE_UPLOAD_LIMIT_FAIL);
    }
}
