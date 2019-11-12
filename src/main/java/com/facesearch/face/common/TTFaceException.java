package com.tt.face.common;

/**
 * 自定义异常类型
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:28
 **/
public class TTFaceException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//错误代码
    ResultCode resultCode;

    public TTFaceException(ResultCode resultCode){
        this.resultCode = resultCode;
    }
    public ResultCode getResultCode(){
        return resultCode;
    }


}
