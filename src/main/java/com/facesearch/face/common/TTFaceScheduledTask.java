package com.tt.face.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/*@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@EnableAsync*/
public class TTFaceScheduledTask {
	
	final static Logger logger = LogManager.getLogger(TTFaceScheduledTask.class);
	
	@Autowired
	StartService startService;
	
	@Value("${face.picurl}")
	private  String picurl;


	//3.添加定时任务
    @Scheduled(cron = "0 0/30 * * * ?")
	//@Scheduled(cron = "0 0/10 * * * ?")
    //或直接指定时间间隔，例如：10分钟
    private void faceRegisterTasks() {
		Long start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		logger.info("=============== faceRegister task start  ===============" );
		startService.faceRegister();
		Long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        logger.info("=============== faceRegister task end " + "=> spend： "+ (end - start) + " =============== ");
    }
	
	/*@Scheduled(cron = "0 0 1 * * ?")
    //指定时间间隔，例如：每天1点执行
    private void delFileTasks() {
		Long start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		logger.info("=============== del temp file task start  ===============" );
		
		String searchFilePathSrc = picurl + File.separator + "search" + File.separator;
		
		File searchFilePath = new File(searchFilePathSrc);
		
		try {
			logger.info("=============== del search file task start  =============== ");
			FileUtils.deleteDirectory(searchFilePath);
			logger.info("=============== del search file task end  =============== ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("=============== del search file task fail  =============== " + e.getMessage());

		}

		
        String compareFilePathSrc = picurl + File.separator + "compare" + File.separator;
        
        File compareFilePath = new File(compareFilePathSrc);
        
        try {
			logger.info("=============== del compare file task start  =============== ");

			FileUtils.deleteDirectory(compareFilePath);
			
			logger.info("=============== del compare file task end  =============== ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("=============== del compare file task fail  =============== " + e.getMessage());

		}
       
        String detectFilePathSrc = picurl + File.separator + "detect" + File.separator;
        
        File detectFilePath = new File(detectFilePathSrc);
        
        try {
			logger.info("=============== del detect file task start  =============== ");

			FileUtils.deleteDirectory(detectFilePath);
			
			logger.info("=============== del detect file task end  =============== ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("=============== del detect file task fail  =============== " + e.getMessage());

		}

		Long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        logger.info("=============== del temp file task end " + "=> spend： "+ (end - start) + " =============== ");
    }*/

}
