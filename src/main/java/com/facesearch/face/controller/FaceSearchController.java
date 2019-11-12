package com.tt.face.controller;

import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seetaface2.model.FaceLandmark;
import com.tt.face.common.CommonCode;
import com.tt.face.common.ExceptionCast;
import com.tt.face.common.ResponseResult;
import com.tt.face.common.TTFaceException;
import com.tt.face.common.TTFaceHelper;
import com.tt.face.common.TTFaceResult;
import com.tt.face.service.TTFaceService;
import com.tt.face.utils.DeleteFileUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "人脸AI系统接口",tags = "人脸AI系统接口")
@RestController
public class FaceSearchController {
	
	
	@Autowired
	TTFaceService ttFaceService;
	
	
	final static Logger logger = LogManager.getLogger(TTFaceController.class);

	
  
    @ApiOperation(value = "人脸搜索")
    @ApiImplicitParams({@ApiImplicitParam(name = "file", value = "文件流对象", required = true,dataType = "__File")})
	@ApiResponses({@ApiResponse(code=10000,message="操作成功"),
		@ApiResponse(code=10003,message="参数非法"),
		@ApiResponse(code=11111,message="操作失败"),
        @ApiResponse(code=99999,message="系统出错")
         })
    @CrossOrigin
	@PostMapping("/facesearch")
    public ResponseResult<TTFaceResult> faceSearch(@RequestParam("file") MultipartFile multifile) throws Exception {
		ResponseResult<TTFaceResult> rr = null;
        rr = ttFaceService.faceSearch(multifile);
    	return  rr;
	         
    }
    
	

}
