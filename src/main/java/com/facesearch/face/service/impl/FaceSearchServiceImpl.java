package com.tt.face.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seetaface2.model.FaceLandmark;
import com.tt.face.ai.FaceHelper;
import com.tt.face.ai.bean.FaceIndex;
import com.tt.face.ai.bean.Result;
import com.tt.face.common.CommonCode;
import com.tt.face.common.ExceptionCast;
import com.tt.face.common.ResponseResult;
import com.tt.face.common.TTFaceException;
import com.tt.face.common.TTFaceHelper;
import com.tt.face.common.TTFaceResult;
import com.tt.face.pojo.User;
import com.tt.face.service.TTFaceService;
import com.tt.face.utils.DeleteFileUtil;
import com.tt.face.utils.TTFileUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ybs
 * @since 2019-09-19
 */
@Service
public class TTFaceServiceImpl  implements TTFaceService {
	

	@Value("${face.comparesimilarity}")
	private  String compareSimilarity;
	
	@Value("${face.searchsimilarity}")
	private  String searchSimilarity;

	
	@Value("${face.picurl}")
	private  String picurl;
	
	final static Logger logger = LogManager.getLogger(TTFaceServiceImpl.class);

	@Transactional
    public ResponseResult updateFace(MultipartFile file,String id) throws Exception{
        if (file.isEmpty() || StringUtils.isEmpty(id)) {
        	logger.info("ttface updateFace file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        String fileName = file.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = file.getContentType();
        
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){		
            logger.info("ttface updateFace file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        String destPath = "";
        try {
	        String filePathSrc = picurl + File.separator;
	        String faceUrl =  "";

	        QueryWrapper<User> userWrapper = new QueryWrapper<>();
	        User uc = new User();
	        uc.setId(Long.valueOf(id));
	        uc.setDeleteflag(0);
	        userWrapper.setEntity(uc);
	        List<User> userlist = uc.selectList(userWrapper);

	        if(userlist == null || userlist.size() == 0) {
	        	ExceptionCast.cast(CommonCode.USER_NO_EXIST);
	        }
	        
		     
	        User userResult = userlist.get(0);
	        
	        if(userResult.getSeetaIndex() >= TTFileUtils.FILE_REPOS_LINMIT)
		    	 ExceptionCast.cast(CommonCode.FACE_REPOS_LIMIT_FAIL);
	        
	         faceUrl =  userResult.getId() + "." + fileFix;        
	         destPath = filePathSrc + faceUrl;
		     File dest = new File(destPath);
		     file.transferTo(dest);
		
	         userResult.setFacePicUrl(faceUrl);
		     FaceIndex fi =  TTFaceHelper.faceRegister(String.valueOf(userResult.getId()), FileUtils.readFileToByteArray(dest),userResult);
	        if(fi == null)
	        	ExceptionCast.cast(CommonCode.FACE_UPLOAD_FAIL);
	        
	         logger.info("ttface updateFace file ： " + "is success");

	        return new ResponseResult(CommonCode.SUCCESS);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	DeleteFileUtil.delete(destPath);
	    	logger.error("ttface updateFace file： " + "is errror");
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	    	throw e;
	    }      
    }
	
	@Transactional
    public ResponseResult register(MultipartFile file, String userstr) throws Exception {
        if (file.isEmpty() || StringUtils.isEmpty(userstr)) {
        	logger.info("ttface register file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String fileName = file.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = file.getContentType();
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){		
        	logger.info("ttface register file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        JSONObject  jsonObject = JSONObject.parseObject(userstr);    
        logger.info("ttface register userstr ： " + jsonObject);
        if(jsonObject.isEmpty()){
        	logger.info("ttface register file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        User user = JSONObject.toJavaObject(jsonObject, User.class);

        String registerPath = "";
        TTFaceResult fr = null;
        ResponseResult rr =  new ResponseResult(CommonCode.SUCCESS);
        try {
        	boolean sqlResult = false;
	        String filePathSrc = picurl + File.separator;
	        String faceUrl =  "";
	        QueryWrapper<User> userWrapper = new QueryWrapper<>();
	        User uc = new User();
	        uc.setName(user.getName());
	        uc.setJobId(user.getJobId());
	        uc.setDeleteflag(0);
	        userWrapper.setEntity(uc);
	        List<User> userlist = user.selectList(userWrapper);
	        if(userlist.size() == 0){
	        	user.setCreateTime(LocalDateTime.now());
	        	sqlResult = user.insert();
	        }else {
	        	ExceptionCast.cast(CommonCode.REGISTER_REPEAT);
	        }
	        
	        if(!sqlResult)
	        	ExceptionCast.cast(CommonCode.REGISTER_FAIL);
	        
	         faceUrl =  user.getId() + "." + fileFix;        
	         registerPath = filePathSrc + faceUrl;
		     File dest = new File(registerPath);
		     file.transferTo(dest);
		     user.setFacePicUrl(faceUrl);
		     FaceIndex fi =  null;
		     try {
			      fi =  TTFaceHelper.faceRegister(String.valueOf(user.getId()), FileUtils.readFileToByteArray(dest),user);
			     if(fi == null)
			     {
			    	 logger.info("ttface image file ： " + "is fail");
			    	 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			    	 ExceptionCast.cast(CommonCode.FACE_UPLOAD_FAIL);
			     }
		     } catch (Exception e) {
			    	e.printStackTrace();
			    	DeleteFileUtil.delete(registerPath);
			    	logger.error("ttface image file is errror： " + e.getMessage());
			    	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			    	if(e instanceof TTFaceException)
				    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
			    	throw e;
			  }
	         /*user.setSeetaIndex(fi.getIndex());
	         sqlResult = user.updateById();*/
	         logger.info("ttface register function： " + "is success");  
	         fr = new TTFaceResult(user,"","");
	         rr.setData(fr);
	        return rr;
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	logger.error("ttface reister file： " + e.getMessage());
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	    	throw e;
	    }
     
    }
	
	@Transactional
    public ResponseResult registerByFileBase64(String fileBase64, String userstr) throws Exception {
		
		MultipartFile multifile = null;
        if (StringUtils.isEmpty(fileBase64)) {
        	logger.info("ttface registerByFileBase64 file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        multifile = TTFileUtils.base64ToMultipart(fileBase64);
        
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = multifile.getContentType();
        
        logger.info("ttface registerByFileBase64 file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
      
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){		
        	logger.info("ttface registerByFileBase64 file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        JSONObject  jsonObject = JSONObject.parseObject(userstr);    
        logger.info("ttface registerByFileBase64 userstr ： " + jsonObject);
        if(jsonObject.isEmpty()){
        	logger.info("ttface registerByFileBase64 file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        User user = JSONObject.toJavaObject(jsonObject, User.class);

        String registerPath = "";
        TTFaceResult fr = null;
        ResponseResult rr =  new ResponseResult(CommonCode.SUCCESS);
        try {
        	boolean sqlResult = false;
	        String filePathSrc = picurl + File.separator;
	        String faceUrl =  "";
	        QueryWrapper<User> userWrapper = new QueryWrapper<>();
	        User uc = new User();
	        uc.setName(user.getName());
	        uc.setJobId(user.getJobId());
	        uc.setDeleteflag(0);
	        userWrapper.setEntity(uc);
	        List<User> userlist = user.selectList(userWrapper);
	        if(userlist.size() == 0){
	        	user.setCreateTime(LocalDateTime.now());
	        	sqlResult = user.insert();
	        }else {
	        	ExceptionCast.cast(CommonCode.REGISTER_REPEAT);
	        }
	        
	        if(!sqlResult)
	        	ExceptionCast.cast(CommonCode.REGISTER_FAIL);
	        
	         faceUrl =  user.getId() + "." + fileFix;        
	         registerPath = filePathSrc + faceUrl;
		     File dest = new File(registerPath);
		     multifile.transferTo(dest);
		     user.setFacePicUrl(faceUrl);
		     FaceIndex fi =  null;
		     try {
			      fi =  TTFaceHelper.faceRegister(String.valueOf(user.getId()), FileUtils.readFileToByteArray(dest),user);
			     if(fi == null)
			     {
			    	 logger.info("ttface image file ： " + "is fail");
			    	 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			    	 ExceptionCast.cast(CommonCode.FACE_UPLOAD_FAIL);
			     }
		     } catch (Exception e) {
			    	e.printStackTrace();
			    	DeleteFileUtil.delete(registerPath);
			    	logger.error("ttface image file is errror： " + e.getMessage());
			    	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			    	if(e instanceof TTFaceException)
				    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
			    	throw e;
			  }
	         /*user.setSeetaIndex(fi.getIndex());
	         sqlResult = user.updateById();*/
	         logger.info("ttface registerByFileBase64 function： " + "is success");  
	         fr = new TTFaceResult(user,"","");
	         rr.setData(fr);
	        return rr;
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	logger.error("ttface registerByFileBase64 file： " + e.getMessage());
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	    	throw e;
	    }
     
    }
/*
	
private	 static class FileData implements Callable<Boolean> {

		    private String srcPath;
		    
		    private String destPath;

		    public FileData(String srcPath,String destPath){
		        this.srcPath = srcPath;
		        this.destPath = destPath;
		    }

		    @Override
		    public Boolean call() throws Exception {
		        //真实的业务逻辑
		        //System.out.println("delete src file ： " + srcPath);
		        logger.info("delete src file ： " + srcPath);
		        boolean srcResult = DeleteFileUtil.delete(srcPath);
		        if(!srcResult){
		        	Thread.sleep(2000);
		        	srcResult = DeleteFileUtil.delete(srcPath);
		        }
		        if(!srcResult){
		        	Thread.sleep(2000);
		        	srcResult = DeleteFileUtil.delete(srcPath);
		        }
		        boolean destResult = false;
		        if(!StringUtils.isEmpty(destPath)){
			        logger.info("delete dest file ： " + destPath);
			        destResult =DeleteFileUtil.delete(destPath);
			        if(!destResult){
			        	Thread.sleep(2000);
			        	destResult = DeleteFileUtil.delete(destPath);
			        }
			        if(!destResult){
			        	Thread.sleep(2000);
			        	destResult = DeleteFileUtil.delete(destPath);
			        }
		        }
		        
		        return srcResult && destResult;
		    }
		 }
	*/
    public ResponseResult<TTFaceResult> faceCompare2(MultipartFile srcMultifile, MultipartFile destMultifile) throws Exception{
        if (srcMultifile.isEmpty() || destMultifile.isEmpty()) {
        	logger.info("ttface compare file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String srcfileName = srcMultifile.getOriginalFilename();
        String srcfileFix = srcfileName.substring(srcfileName.lastIndexOf(".") + 1);
        String srcFileContentType = srcMultifile.getContentType();
    	//logger.info("ttface compare file ： " + "=== srcfileFix >>" + srcfileFix + " === srcfileContentType >>" + srcFileContentType);

       // if(!StringUtils.equals(srcfileFix,"jpg") &&!StringUtils.equals(srcfileFix,"jpeg") && !StringUtils.equals(srcfileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(srcfileFix)){
        	logger.info("ttface compare file ： " + "is invaild=== srcfileFix >>" + srcfileFix + " === srcfileContentType >>" + srcFileContentType);
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        
        String destfileName = destMultifile.getOriginalFilename();
        String destfileFix = destfileName.substring(destfileName.lastIndexOf(".") + 1);
        String destFileContentType = destMultifile.getContentType();
    	//logger.info("ttface compare file ： " + "=== destfileFix >>" + destfileFix + " === destfileContentType >>" + destFileContentType);

        //if(!StringUtils.equals(destfileFix,"jpg") &&!StringUtils.equals(destfileFix,"jpeg") && !StringUtils.equals(destfileFix,"png")){
    	 if(!TTFileUtils.fileTypelist.contains(destfileFix)){	
    	    logger.info("ttface compare file ： " + "is invaild=== destfileFix >>" + destfileFix + " === destfileContentType >>" + destFileContentType);
    	    ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }        
    
        
       /* if(srcfileFix != "jpg" && srcfileFix != "jpeg" && srcfileFix != "png"){
        	logger.info("ttface compare srcfile ： " + "is invaild");
            return new ResponseResult<TTFaceResult>(CommonCode.INVALID_PARAM);
        }
        
        if(destfileFix != "jpg" && destfileFix != "jpeg" && destfileFix != "png"){
        	logger.info("ttface compare destfile ： " + "is invaild");
            return new ResponseResult<TTFaceResult>(CommonCode.INVALID_PARAM);
        }*/
        
        ResponseResult<TTFaceResult> rr =  new ResponseResult<TTFaceResult>(CommonCode.SUCCESS);
        
       /* String filePathSrc = picurl + File.separator + "compare" + File.separator;
        logger.info("ttface compare filePathSrc ： " + filePathSrc);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String filePath = filePathSrc + year + File.separator + month + File.separator + date + File.separator;
        logger.info("ttface compare filePath ： " + filePath);

        String srcfileName = srcMultifile.getOriginalFilename();
        String srcfileFix = srcfileName.substring(srcfileName.lastIndexOf(".") + 1);
        File srcfile = new File(filePath + id + "_" + System.currentTimeMillis() +  "_src." + srcfileFix);
        
        String destfileName = destMultifile.getOriginalFilename();
        String destfileFix = destfileName.substring(destfileName.lastIndexOf(".") + 1);
        File destfile = new File(filePath + id + "_" + System.currentTimeMillis() +  "_dest." + destfileFix);*/
        try {
        	/*TTFileUtils.multipartFile2File(srcMultifile, srcfile);
        	TTFileUtils.multipartFile2File(destMultifile, destfile);*/
        	long l = System.currentTimeMillis();
        	float similarity = TTFaceHelper.compare(TTFileUtils.multipartFile2BufferedImage(srcMultifile), TTFileUtils.multipartFile2BufferedImage(destMultifile));
            logger.info("compare data=>" + similarity + "， time=>" + (System.currentTimeMillis() - l));

        	BigDecimal dataBigDec = new BigDecimal(Float.toString(similarity));    
        	BigDecimal lineBigDec = new BigDecimal(compareSimilarity);    
        	int result = dataBigDec.compareTo(lineBigDec);
        	logger.info("ttface compare file result ： " + result);
        	
        	

        	 TTFaceResult fr = new TTFaceResult(null,String.valueOf(result),String.valueOf(similarity));
	         rr.setData(fr);
        	
        	/*try {
		        	FutureTask<Boolean> future = new FutureTask<Boolean>(new FileData(srcfile.getAbsolutePath(),destfile.getAbsolutePath()));
		    		future.run();
        	} catch (Exception e) {
    	    	e.printStackTrace();   
    	    }*/
    		return rr;
	    } catch (Exception e) {
	    	e.printStackTrace(); 
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	        throw e;
	    }      
    }
    
    public ResponseResult<TTFaceResult> faceSearchByFileBase64(String fileBase64) throws Exception {
    	MultipartFile multifile = null;
        if (StringUtils.isEmpty(fileBase64)) {
        	logger.info("ttface search file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        multifile = TTFileUtils.base64ToMultipart(fileBase64);
        
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = multifile.getContentType();
        
        logger.info("ttface faceSearchByFileBase64 file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
        
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){	
            logger.info("ttface faceSearchByFileBase64 file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        TTFaceResult fr = null;
        ResponseResult<TTFaceResult> rr =  new ResponseResult<TTFaceResult>(CommonCode.SUCCESS);
      /*  String filePathSrc = picurl + File.separator + "search" + File.separator;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String filePath = filePathSrc + year + File.separator + month + File.separator + date + File.separator;
        logger.info("ttface search filePath ： " + filePath);
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(filePath + id + "_" + System.currentTimeMillis() +  "." + fileFix);*/
        
        try {
        	//TTFileUtils.multipartFile2File(multifile, file);
        	long l = System.currentTimeMillis();
        	//Result recResult = FaceHelper.search(FileUtils.readFileToByteArray(file));
        	Result recResult = TTFaceHelper.search(TTFileUtils.multipartFile2BufferedImage(multifile));

            if(recResult == null){
            	 logger.info("faceSearchByFileBase64 result：recResult=>" + recResult + "， time=>"  + (System.currentTimeMillis() - l));
            	fr = new TTFaceResult(null,String.valueOf("-1"),"0");
            	rr.setData(fr);
            	return rr;
            }
            
            System.out.println("搜索结果：key=>" + recResult.getKey() + " similar=>"+recResult.getSimilar()+"， time=>" + (System.currentTimeMillis() - l));
            logger.info("faceSearchByFileBase64 result：key=>" + recResult.getKey() + " similar=>"+recResult.getSimilar()+"， time=>" + (System.currentTimeMillis() - l));

        	float similar = recResult.getSimilar();
        	BigDecimal dataBigDec = new BigDecimal(similar);    
        	BigDecimal lineBigDec = new BigDecimal(searchSimilarity);    
        	int result = dataBigDec.compareTo(lineBigDec);
        	logger.info("ttface faceSearchByFileBase64 file result ： " + result);
        	
            if(result>=0){
            	QueryWrapper<User> userWrapper = new QueryWrapper<>();
    	        User uc = new User();
    	        uc.setSeetaIndex(Integer.valueOf(recResult.getKey()));
    	        uc.setDeleteflag(0);
    	        userWrapper.setEntity(uc);
    	        List<User> userlist = uc.selectList(userWrapper);
    	        if(userlist.size() != 0) {
    	        	fr = new TTFaceResult(userlist.get(userlist.size()-1),String.valueOf(result),String.valueOf(similar));
    	        	rr.setData(fr);
                	return rr;
    	        }

            }

            fr = new TTFaceResult(null,String.valueOf(result),String.valueOf(similar));
        	rr.setData(fr);	
        	
        	/*try {
		        	FutureTask<Boolean> future = new FutureTask<Boolean>(new FileData(file.getAbsolutePath(),""));
		    		future.run();
        	} catch (Exception e) {
    	    	e.printStackTrace();   
    	    }*/
    		return rr;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	        throw e;
	    }
      
    }
	
    
	
    public ResponseResult<TTFaceResult> faceSearch(MultipartFile multifile) throws Exception {
        if (multifile.isEmpty()) {
        	logger.info("ttface search file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = multifile.getContentType();
        
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){	
            logger.info("ttface search file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        TTFaceResult fr = null;
        ResponseResult<TTFaceResult> rr =  new ResponseResult<TTFaceResult>(CommonCode.SUCCESS);
      /*  String filePathSrc = picurl + File.separator + "search" + File.separator;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String filePath = filePathSrc + year + File.separator + month + File.separator + date + File.separator;
        logger.info("ttface search filePath ： " + filePath);
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(filePath + id + "_" + System.currentTimeMillis() +  "." + fileFix);*/
        
        try {
        	//TTFileUtils.multipartFile2File(multifile, file);
        	long l = System.currentTimeMillis();
        	//Result recResult = FaceHelper.search(FileUtils.readFileToByteArray(file));
        	Result recResult = TTFaceHelper.search(TTFileUtils.multipartFile2BufferedImage(multifile));

            if(recResult == null){
            	 logger.info("search result：recResult=>" + recResult + "， time=>"  + (System.currentTimeMillis() - l));
            	fr = new TTFaceResult(null,String.valueOf("-1"),"0");
            	rr.setData(fr);
            	return rr;
            }
            
            System.out.println("搜索结果：key=>" + recResult.getKey() + " similar=>"+recResult.getSimilar()+"， time=>" + (System.currentTimeMillis() - l));
            logger.info("search result：key=>" + recResult.getKey() + " similar=>"+recResult.getSimilar()+"， time=>" + (System.currentTimeMillis() - l));

        	float similar = recResult.getSimilar();
        	BigDecimal dataBigDec = new BigDecimal(similar);    
        	BigDecimal lineBigDec = new BigDecimal(searchSimilarity);    
        	int result = dataBigDec.compareTo(lineBigDec);
        	logger.info("ttface search file result ： " + result);
        	
            if(result>=0){
            	QueryWrapper<User> userWrapper = new QueryWrapper<>();
    	        User uc = new User();
    	        uc.setSeetaIndex(Integer.valueOf(recResult.getKey()));
    	        uc.setDeleteflag(0);
    	        userWrapper.setEntity(uc);
    	        List<User> userlist = uc.selectList(userWrapper);
    	        if(userlist.size() != 0) {
    	        	fr = new TTFaceResult(userlist.get(userlist.size()-1),String.valueOf(result),String.valueOf(similar));
    	        	rr.setData(fr);
                	return rr;
    	        }

            }

            fr = new TTFaceResult(null,String.valueOf(result),String.valueOf(similar));
        	rr.setData(fr);	
        	
        	/*try {
		        	FutureTask<Boolean> future = new FutureTask<Boolean>(new FileData(file.getAbsolutePath(),""));
		    		future.run();
        	} catch (Exception e) {
    	    	e.printStackTrace();   
    	    }*/
    		return rr;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	if(e instanceof TTFaceException)
		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
	        throw e;
	    }
      
    }
    
    
    public ResponseResult<FaceLandmark>  detectLandmark(MultipartFile multifile,String id) throws Exception{
        if (multifile.isEmpty()|| StringUtils.isEmpty(id)) {
        	logger.info("ttface detectLandmark file ： " + "is empty");
        	ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        String fileName = multifile.getOriginalFilename();
        String fileFix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileContentType = multifile.getContentType();
        
        //if(!StringUtils.equals(fileFix,"jpg") &&!StringUtils.equals(fileFix,"jpeg") && !StringUtils.equals(fileFix,"png")){
        if(!TTFileUtils.fileTypelist.contains(fileFix)){	
            logger.info("ttface detectLandmark file ： " + "is invaild=== fileFix >>" + fileFix + " === fileContentType >>" + fileContentType);
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        
        TTFaceResult fr = null;
        ResponseResult<FaceLandmark> rr =  new ResponseResult<FaceLandmark>(CommonCode.SUCCESS);

       
             try{
             	long l = System.currentTimeMillis();
                
             	FaceLandmark faceLandmark = TTFaceHelper.detectLandmark(TTFileUtils.multipartFile2BufferedImage(multifile));
                 
                 if(faceLandmark == null){
                 	 logger.info("detectLandmark result：recResult  time=>"  + (System.currentTimeMillis() - l));
                 	ExceptionCast.cast(CommonCode.DETECT_FAIL);
                 }
                 String faceLandmarkJSON = JSONObject.toJSONString(faceLandmark);
                 System.out.println("检测结果：detectLandmark =>" + faceLandmarkJSON +"， time=>" + (System.currentTimeMillis() - l));
                 logger.info("detectLandmark result：=>" + faceLandmarkJSON +"， time=>" + (System.currentTimeMillis() - l));

                 rr.setData(faceLandmark);
        
         		return rr;
     	    } catch (Exception e) {
     	    	e.printStackTrace();
     	    	if(e instanceof TTFaceException)
    		    	ExceptionCast.cast(((TTFaceException)e).getResultCode());
     	    	throw e;
     	    }
    }

	@Override
	@Transactional
	public void clearInit() throws Exception {
		try{
         	 long l = System.currentTimeMillis();
         	 
         	 QueryWrapper<User> userWrapper = new QueryWrapper<>();
    	     User user = new User();
    	     user.setDeleteflag(1);
    	     user.setUpdateTime(LocalDateTime.now());
    	     userWrapper.setEntity(user);
    	     boolean result =  user.update(userWrapper);
 
	        if(!result) 
	        	ExceptionCast.cast(CommonCode.FAIL);
            
         	 TTFaceHelper.clearInit();

 	    } catch (Exception e) {
 	    	e.printStackTrace();
 	    	throw e;
 	    }
	}

}
