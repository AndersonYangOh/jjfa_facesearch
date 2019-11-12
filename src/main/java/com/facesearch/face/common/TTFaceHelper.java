package com.tt.face.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seetaface2.model.FaceLandmark;
import com.seetaface2.model.RecognizeResult;
import com.seetaface2.model.SeetaImageData;
import com.seetaface2.model.SeetaRect;
import com.tt.face.ai.FaceHelper;
import com.tt.face.ai.bean.FaceIndex;
import com.tt.face.ai.bean.Result;
import com.tt.face.ai.utils.ImageUtils;
import com.tt.face.pojo.User;
import com.tt.face.utils.TTFileUtils;

@Component
public class TTFaceHelper extends FaceHelper{
	
	
	private static String searchRepoUrl;
		
    private static Logger logger = LoggerFactory.getLogger(TTFaceHelper.class);


	public TTFaceHelper() {
		super();
	}

	@Value("${face.searchrepo}")
    public void setSearchRepo(String searchRepo) {
		searchRepoUrl = searchRepo;
	}
	  /**
     * 注册人脸(会对人脸进行裁剪)
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    public static FaceIndex faceRegister(String key, byte[] img) throws IOException {
    	
    	FaceIndex face = null;
    	
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        //先对人脸进行裁剪
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        byte[] bytes = seeta.crop(imageData);

        if (bytes == null || bytes.length != CROP_SIZE) {
            logger.info("register face fail: key={}, error=no valid face", key);
            return face;
        }
        imageData = new SeetaImageData(256, 256, 3);
        imageData.data = bytes;
        int index = seeta.register(imageData);
        if (index < 0) {
            logger.info("register face fail: key={}, index={}", key, index);
            return face;
        }
        if(index >= TTFileUtils.FILE_REPOS_LINMIT){
        	ExceptionCast.cast(CommonCode.FACE_UPLOAD_LIMIT_FAIL);
        }
        face = new FaceIndex();
        face.setKey(key);
        face.setImgData(imageData.data);
        face.setWidth(imageData.width);
        face.setHeight(imageData.height);
        face.setChannel(imageData.channels);
        face.setIndex(index);
        //FaceDao.saveOrUpdate(face);
        logger.info("Register face success: key={}, index={}", key, index);
        return face;
    }
    
    /**
     * 注册人脸(会对人脸进行裁剪)
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    @Transactional
    public static FaceIndex faceRegister(String key, byte[] img,User user) throws Exception {
    	
    	FaceIndex face = null;
    	
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        //先对人脸进行裁剪
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        byte[] bytes = seeta.crop(imageData);

        if (bytes == null || bytes.length != CROP_SIZE) {
            logger.info("register face fail: key={}, error=no valid face", key + "==> bytes is " + bytes);
            return face;
        }
        imageData = new SeetaImageData(256, 256, 3);
        imageData.data = bytes;
        int index = seeta.register(imageData);
        if (index < 0) {
            logger.info("register face fail: key={}, index={}", key, index);
            return face;
        }
        if(index >= TTFileUtils.FILE_REPOS_LINMIT){
        	ExceptionCast.cast(CommonCode.FACE_UPLOAD_LIMIT_FAIL);
        }
        face = new FaceIndex();
        face.setKey(key);
        face.setImgData(imageData.data);
        face.setWidth(imageData.width);
        face.setHeight(imageData.height);
        face.setChannel(imageData.channels);
        face.setIndex(index);
       // FaceDao.saveOrUpdate(face);
        
        user.setSeetaIndex(index);
    	user.setUpdateTime(LocalDateTime.now());
        boolean result = user.updateById();
        if(!result) face = null;
        logger.info("Register face success: key={}, index={}", key, index);
        return face;
    }
    
    /**
     * 注册人脸(会对人脸进行裁剪)
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return
     * @throws IOException
     */
    @Transactional
    public static FaceIndex faceRegister(String key, byte[] img,Long id) throws Exception {
    	
    	 FaceIndex face = null;
    	
    	 QueryWrapper<User> userWrapper = new QueryWrapper<>();
	     User user = new User();
	     user.setId(id);
	     user.setDeleteflag(0);
	     userWrapper.setEntity(user);
         List<User> userlist = user.selectList(userWrapper);
        
        if(userlist != null && userlist.size()!=0){
    	
	        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
	        //先对人脸进行裁剪
	        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
	        imageData.data = ImageUtils.getMatrixBGR(image);
	        byte[] bytes = seeta.crop(imageData);
	
	        if (bytes == null || bytes.length != CROP_SIZE) {
	            logger.info("register face fail: key={}, error=no valid face", key);
	            return face;
	        }
	        imageData = new SeetaImageData(256, 256, 3);
	        imageData.data = bytes;
	        int index = seeta.register(imageData);
	        if (index < 0) {
	            logger.info("register face fail: key={}, index={}", key, index);
	            return face;
	        }
	        if(index >= TTFileUtils.FILE_REPOS_LINMIT){
	        	ExceptionCast.cast(CommonCode.FACE_UPLOAD_LIMIT_FAIL);
	        }
	        face = new FaceIndex();
	        face.setKey(key);
	        face.setImgData(imageData.data);
	        face.setWidth(imageData.width);
	        face.setHeight(imageData.height);
	        face.setChannel(imageData.channels);
	        face.setIndex(index);
	        //FaceDao.saveOrUpdate(face);
	        
	        logger.info("seeta register data : key={}, index={}", key, index);
	        
	        user.setSeetaIndex(index);
	    	user.setUpdateTime(LocalDateTime.now());
	        boolean result = user.updateById();

	        if(!result) face = null;
	        logger.info("Register face success: key={}, index={}", key, index);
        }
        return face;
    }

    /**
     * 注册人脸(不裁剪图片)
     *
     * @param key 人脸照片唯一标识
     * @param image 人脸照片
     * @return
     * @throws IOException
     */
    public static FaceIndex faceRegister(String key, BufferedImage image) throws IOException {
    	FaceIndex face = null;
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        int index = seeta.register(imageData);
        if (index < 0) {
            logger.info("register face fail: key={}, index={}", key, index);
            return face;
        }
        if(index >= TTFileUtils.FILE_REPOS_LINMIT){
        	ExceptionCast.cast(CommonCode.FACE_UPLOAD_LIMIT_FAIL);
        }
        face = new FaceIndex();
        face.setKey(key);
        face.setImgData(imageData.data);
        face.setWidth(imageData.width);
        face.setHeight(imageData.height);
        face.setChannel(imageData.channels);
        //FaceDao.saveOrUpdate(face);
        face.setIndex(index);
        logger.info("Register face success: key={}, index={}", key, index);
        return face;
    }
    
    
    public  static void faceRegister() {

         //将人脸图片注册到人脸库中
        //将D:\faces目录下的jpg、png图片都注册到人脸库中
        try {
        	Collection<File> files = FileUtils.listFiles(new File(searchRepoUrl),TTFileUtils.fileTypelist.toArray(new String[TTFileUtils.fileTypelist.size()]), false);
            for (File file : files) {
            	String key = file.getName();
                // int index = FaceHelper.register(FileUtils.readFileToByteArray(file));
            	faceRegister(key, FileUtils.readFileToByteArray(file),Long.valueOf(key.substring(0,key.lastIndexOf("."))));

            }
        } catch (Exception e) {
        	logger.info("=========== register face repo fail  ============= " + e.getMessage());
            e.printStackTrace();
        }
      
    }


   
    /**
     * 重新建立索引
     * @param keys
     */
    public static void faceBuildIndex() {
    	//SeetafaceBuilder.buildIndex();//重新建立索引
    	 TTFaceHelper.faceRegister();
    }
    
    /**
     * 删除已注册的人脸
     * @param keys
     */
    public static void removeRegister(String... keys) {
        //FaceDao.deleteFaceImg(keys);//删除数据库的人脸
        //SeetafaceBuilder.buildIndex();//重新建立索引
        TTFaceHelper.faceRegister();
    }
    
    /**
     * 搜索人脸
     *
     * @param image 人脸照片
     * @return
     * @throws IOException
     */
    public static Result search(BufferedImage image) {
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        RecognizeResult rr = seeta.recognize(imageData);
        System.out.println(" in TTFaceHelper search1 ================ " + rr);
        if (rr == null || rr.index == -1) {
            return null;
        }
        System.out.println(" in TTFaceHelper search2 ================ " + rr.index);
        Result result = new Result(rr);
        result.setKey(String.valueOf(rr.index));
        return result;
    }
    
    /**
     * 人脸特征识别
     *
     * @param image
     * @return
     */
    public static FaceLandmark detectLandmark(BufferedImage image) {
        if (image == null) {
            return null;
        }
        SeetaImageData imageData = new SeetaImageData(image.getWidth(), image.getHeight(), 3);
        imageData.data = ImageUtils.getMatrixBGR(image);
        SeetaRect[] rects = seeta.detect(imageData);
        if (rects == null) {
            return null;
        }
        FaceLandmark faces = new FaceLandmark();
        faces.rects = rects;
        faces.points = seeta.detect(imageData, rects);
        return faces;
    }
    
    /**
     * 人脸比对
     *
     * @param image1
     * @param image2
     * @return 相似度
     */
    public static float compare(BufferedImage image1, BufferedImage image2) {
        if (image1 == null || image2 == null) {
            return 0;
        }
        SeetaImageData imageData1 = new SeetaImageData(image1.getWidth(), image1.getHeight(), 3);
        imageData1.data = ImageUtils.getMatrixBGR(image1);

        SeetaImageData imageData2 = new SeetaImageData(image2.getWidth(), image2.getHeight(), 3);
        imageData2.data = ImageUtils.getMatrixBGR(image2);

        return seeta.compare(imageData1, imageData2);
    }
    
    public static void clearInit() {
        seeta.clear();
        new StartService().faceRegister();
        System.out.println(" in TTFaceHelper clean ================ ");

    }
	
    

}
