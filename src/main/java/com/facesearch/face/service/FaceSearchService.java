package com.tt.face.service;

import org.springframework.web.multipart.MultipartFile;

import com.seetaface2.model.FaceLandmark;
import com.tt.face.common.ResponseResult;
import com.tt.face.common.TTFaceResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ybs
 * @since 2019-09-19
 */
public interface TTFaceService{

	public ResponseResult<TTFaceResult> updateFace(MultipartFile file,String id) throws Exception;
	
	public ResponseResult<TTFaceResult> register(MultipartFile file,String userStr) throws Exception;
	
    public ResponseResult<TTFaceResult> registerByFileBase64(String fileBase64,String userStr)  throws Exception;
	
	public ResponseResult<TTFaceResult> faceCompare2(MultipartFile srcMultifile, MultipartFile destMultifile) throws Exception;
	
    public ResponseResult<TTFaceResult> faceSearch(MultipartFile multifile) throws Exception;
    
    public ResponseResult<TTFaceResult> faceSearchByFileBase64(String fileBase64) throws Exception;
    
    public ResponseResult<FaceLandmark> detectLandmark(MultipartFile multifile,String id) throws Exception;
    
    public void clearInit() throws Exception;


	
}
