package com.tt.face;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class TTFaceThreadTest {
	


	/**
	 * @description:
	 * @author: lbYue
	 * @date: Created in 14:11 2018/1/9
	 */
	public static class RealData implements Callable<String> {

	    private String para;

	    public RealData(String para){
	        this.para = para;
	    }

	    @Override
	    public String call() throws Exception {
	        //真实的业务逻辑
	        System.out.println(para);
	        
	        Thread.sleep(500);
	        return para;
	    }
	}
	

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		for(int i = 0;i < 100 ;i ++){

		/*FutureTask<String> future = new FutureTask<String>(new RealData("i"+i));
		
		future.run();*/
	 
       /* ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(future);
        
        executor.shutdown();*/
	   }
        System.out.println("请求完毕！");
        try {
           // System.out.println("我在睡觉别打扰我！2秒后回应");
            //Thread.sleep(2000);
        }catch (Exception e){
        	e.printStackTrace();
        }
        
        String fileName = "555avi";
        
        
        
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String filePath =   year +"\\" + month + "\\" + date + "\\";
        
        System.out.println(filePath);
       //
       // System.out.println("数据处理完成："+future.get());
		
	}

}
