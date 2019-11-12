package com.tt.face.common;

import com.tt.face.pojo.User;

import io.swagger.annotations.ApiModelProperty;

public class TTFaceResult {
	
	public TTFaceResult(User user, String result, String similarity) {
		this.user = user;
		this.result = result;
		this.similarity = similarity;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	@ApiModelProperty(value = "用户信息",required = true)
	private User user;
	
	@ApiModelProperty(value = "结果，范围[-1,0,1]: -1为失败",required = true)
	private String result;
	
	@ApiModelProperty(value = "相似度",required = true)
	private String similarity;

	public String getSimilarity() {
		return similarity;
	}
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}



}
