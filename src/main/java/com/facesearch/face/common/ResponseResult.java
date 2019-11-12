package com.tt.face.common;

import io.swagger.annotations.ApiModelProperty;

public class ResponseResult<T> {
	//操作是否成功
    @ApiModelProperty(value = "成功标识")
    boolean success = true;

    //操作代码
    @ApiModelProperty(value = "返回代码")
    int code = 10000;

    //提示信息
    @ApiModelProperty(value = "提示信息")
    String message;
    
    @ApiModelProperty(value = "返回对象")
    private T data;

    public ResponseResult(ResultCode resultCode){
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.message = resultCode.message();
    }
    
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ResponseResult SUCCESS(){
        return new ResponseResult(CommonCode.SUCCESS);
    }
    public static ResponseResult FAIL(){
        return new ResponseResult(CommonCode.FAIL);
    }
}
