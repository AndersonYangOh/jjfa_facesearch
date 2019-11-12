package com.tt.face.common;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tt.face.ai.SeetafaceBuilder;
import com.tt.face.utils.TTFileUtils;

@Component
public class StartService implements ApplicationRunner {
	
	@Value("${face.searchrepo}")
	private String searchRepo;
	
	final static Logger logger = LogManager.getLogger(StartService.class);
	
	public  void faceRegister() {
        SeetafaceBuilder.build();//系统启动时先调用初始化方法

        //等待初始化完成
        /*while (SeetafaceBuilder.getFaceDbStatus() == SeetafaceBuilder.FacedbStatus.LOADING || SeetafaceBuilder.getFaceDbStatus() == SeetafaceBuilder.FacedbStatus.READY) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        //将人脸图片注册到人脸库中
        //将D:\faces目录下的jpg、png图片都注册到人脸库中
        try {
        	Collection<File> files = FileUtils.listFiles(new File(searchRepo),TTFileUtils.fileTypelist.toArray(new String[TTFileUtils.fileTypelist.size()]), false);
            for (File file : files) {
            	String key = file.getName();
                // int index = FaceHelper.register(FileUtils.readFileToByteArray(file));
            	TTFaceHelper.faceRegister(key, FileUtils.readFileToByteArray(file),Long.valueOf(key.substring(0,key.lastIndexOf("."))));

            }
        } catch (Exception e) {
        	logger.info("=========== register face repo fail  ============= " + e.getMessage());
            e.printStackTrace();
        }
      
    }
	

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("=========== face register start =============");
		faceRegister();
		logger.info("=========== face project start =============");
	}
	
}

