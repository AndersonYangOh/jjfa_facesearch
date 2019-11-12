package com.tt.face.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TTFileUtils {
	
	public static List<String> fileTypelist = new ArrayList<String>(Arrays.asList("jpg", "jpeg", "png"));
	
	public static int FILE_REPOS_LINMIT = 490;
    
	public static MultipartFile base64ToMultipart(String fileBase64) throws Exception {
		
	    String[] baseStrs = fileBase64.split(",");

	    byte[] b = new byte[0];
		b = Base64.getDecoder().decode(baseStrs[1]);

		for(int i = 0; i < b.length; ++i) {
		    if (b[i] < 0) {
		        b[i] += 256;
		    }
		}
		
	/*	String imgFilePath = "d://testbase64.jpg";// 新生成的图片
		OutputStream out = new FileOutputStream(imgFilePath);
		out.write(b);
		out.flush();
		out.close();*/


		return new BASE64DecodedMultipartFile(b, baseStrs[0]);
	}

	
	 public static BufferedImage multipartFile2BufferedImage(MultipartFile file) {  
	        BufferedImage srcImage = null;  
	        try {  
	            InputStream in = (InputStream) file.getInputStream();
	            srcImage = javax.imageio.ImageIO.read(in);  
	        } catch (IOException e) {
	        	e.printStackTrace();
	            System.out.println("read image is error ！ " + e.getMessage());  
	        }
	        return srcImage;  
	    }
	
	public static MultipartFile file2MultipartFile(File file) { 
        FileItem fileItem = createFileItem(file.getPath(),file.getName()); 
        MultipartFile mfile = new CommonsMultipartFile(fileItem);  
        return mfile;  
    }
	
	public static void multipartFile2File(MultipartFile multfile,File file) throws IOException { 
		FileUtils.copyInputStreamToFile(multfile.getInputStream(), file);  
    }
	
	private static FileItem createFileItem(String filePath,String fileName){  
        String fieldName = "file";
        FileItemFactory factory = new DiskFileItemFactory(16, null);   
        FileItem item = factory.createItem(fieldName, "text/plain", false,fileName);  
        File newfile = new File(filePath);  
        int bytesRead = 0;  
        byte[] buffer = new byte[8192];  
        try  
        {  
            FileInputStream fis = new FileInputStream(newfile);  
            OutputStream os = item.getOutputStream();  
            while ((bytesRead = fis.read(buffer, 0, 8192))!= -1)  
            {  
                os.write(buffer, 0, bytesRead);  
            }  
            os.close();  
            fis.close();  
        }  
        catch (IOException e)  
        {  
            e.printStackTrace();  
        }  
        return item;  
    }
	
	public static Map<String, String> httpPostRequest(String url, List<MultipartFile> multipartFiles,String fileParName,
	        Map<String, Object> params, int timeout) {
	    Map<String, String> resultMap = new HashMap<String, String>();
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    String result = "";
	    try {
	    HttpPost httpPost = new HttpPost(url);
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
	        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	        String fileName = null;
	        MultipartFile multipartFile = null;
	        for (int i = 0; i < multipartFiles.size(); i++) {
	            multipartFile = multipartFiles.get(i);
	            fileName = multipartFile.getOriginalFilename();
	            builder.addBinaryBody(fileParName, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
	        }
	        //决中文乱码
	        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, MIME.UTF8_CHARSET);
	        for (Map.Entry<String, Object> entry : params.entrySet()) {
	            if(entry.getValue() == null)
	                continue;
	            // 类似浏览器表单提交，对应input的name和value
	            builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
	        }
	        HttpEntity entity = builder.build();
	        httpPost.setEntity(entity);
	        HttpResponse response = httpClient.execute(httpPost);// 执行提交

	        // 设置连接超时时间
	        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
	                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
	        httpPost.setConfig(requestConfig);

	        HttpEntity responseEntity = response.getEntity();
	        resultMap.put("scode", String.valueOf(response.getStatusLine().getStatusCode()));
	        resultMap.put("data", "");
	        if (responseEntity != null) {
	            // 将响应内容转换为字符串
	            result = EntityUtils.toString(responseEntity, java.nio.charset.Charset.forName("UTF-8"));
	            resultMap.put("data", result);
	        }
	    } catch (Exception e) {
	        resultMap.put("scode", "error");
	        resultMap.put("data", "HTTP请求出现异常: " + e.getMessage());

	        Writer w = new StringWriter();
	        e.printStackTrace(new PrintWriter(w));
	        //logger.error("HTTP请求出现异常: " + w.toString());
	    } finally {
	        try {
	            httpClient.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return resultMap;
	}
	
	public static Map<String, String> httpPostRequest2(String url, List<MultipartFile> multipartFiles,String fileParName,List<MultipartFile> multipartFiles2,String fileParName2,
	        Map<String, Object> params, int timeout) {
	    Map<String, String> resultMap = new HashMap<String, String>();
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    String result = "";
	    try {
	    HttpPost httpPost = new HttpPost(url);
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
	        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	        String fileName = null;
	        MultipartFile multipartFile = null;
	        for (int i = 0; i < multipartFiles.size(); i++) {
	            multipartFile = multipartFiles.get(i);
	            fileName = multipartFile.getOriginalFilename();
	            builder.addBinaryBody(fileParName, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
	        }
	        
	        String fileName2 = null;
	        MultipartFile multipartFile2 = null;
	        for (int i = 0; i < multipartFiles2.size(); i++) {
	            multipartFile2 = multipartFiles2.get(i);
	            fileName2 = multipartFile2.getOriginalFilename();
	            builder.addBinaryBody(fileParName2, multipartFile2.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName2);// 文件流
	        }
	        //决中文乱码
	        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, MIME.UTF8_CHARSET);
	        for (Map.Entry<String, Object> entry : params.entrySet()) {
	            if(entry.getValue() == null)
	                continue;
	            // 类似浏览器表单提交，对应input的name和value
	            builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
	        }
	        HttpEntity entity = builder.build();
	        httpPost.setEntity(entity);
	        HttpResponse response = httpClient.execute(httpPost);// 执行提交

	        // 设置连接超时时间
	        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
	                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
	        httpPost.setConfig(requestConfig);

	        HttpEntity responseEntity = response.getEntity();
	        resultMap.put("scode", String.valueOf(response.getStatusLine().getStatusCode()));
	        resultMap.put("data", "");
	        if (responseEntity != null) {
	            // 将响应内容转换为字符串
	            result = EntityUtils.toString(responseEntity, java.nio.charset.Charset.forName("UTF-8"));
	            resultMap.put("data", result);
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        resultMap.put("scode", "error");
	        resultMap.put("data", "HTTP请求出现异常: " + e.getMessage());

	        Writer w = new StringWriter();
	        e.printStackTrace(new PrintWriter(w));
	        //logger.error("HTTP请求出现异常: " + w.toString());
	    } finally {
	        try {
	            httpClient.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return resultMap;
	}
	
	
	public static Map<String, String> httpPostRequestJson(String url, List<MultipartFile> multipartFiles,String fileParName,
	        Map<String, Object> params, int timeout) {
	    Map<String, String> resultMap = new HashMap<String, String>();
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    String result = "";
	    try {
	    HttpPost httpPost = new HttpPost(url);
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
	        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	        String fileName = null;
	        MultipartFile multipartFile = null;
	        for (int i = 0; i < multipartFiles.size(); i++) {
	            multipartFile = multipartFiles.get(i);
	            fileName = multipartFile.getOriginalFilename();
	            builder.addBinaryBody(fileParName, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
	        }
	        //决中文乱码
	       // ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, MIME.UTF8_CHARSET);
	        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, MIME.UTF8_CHARSET);
	        ObjectMapper objectMapper = new ObjectMapper();
	    	String entrtyJson = objectMapper.writeValueAsString(params);
	    	//StringEntity strEntity = new StringEntity(entrtyJson, "UTF-8");
	    	System.out.println("json: " + entrtyJson);
	    	builder.addTextBody("userjson", entrtyJson, contentType);
	    	builder.addTextBody("fileBase64", (String) params.get("fileBase64"), contentType);
	    	
	    	
	    	
	        /*for (Map.Entry<String, Object> entry : params.entrySet()) {
	            if(entry.getValue() == null)
	                continue;
	            // 类似浏览器表单提交，对应input的name和value
	            builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
	        }*/
	        HttpEntity entity = builder.build();
	        httpPost.setEntity(entity);
	        HttpResponse response = httpClient.execute(httpPost);// 执行提交

	        // 设置连接超时时间
	        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
	                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
	        httpPost.setConfig(requestConfig);

	        HttpEntity responseEntity = response.getEntity();
	        resultMap.put("scode", String.valueOf(response.getStatusLine().getStatusCode()));
	        resultMap.put("data", "");
	        if (responseEntity != null) {
	            // 将响应内容转换为字符串
	            result = EntityUtils.toString(responseEntity, java.nio.charset.Charset.forName("UTF-8"));
	            resultMap.put("data", result);
	        }
	    } catch (Exception e) {
	        resultMap.put("scode", "error");
	        resultMap.put("data", "HTTP请求出现异常: " + e.getMessage());

	        Writer w = new StringWriter();
	        e.printStackTrace(new PrintWriter(w));
	        //logger.error("HTTP请求出现异常: " + w.toString());
	    } finally {
	        try {
	            httpClient.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return resultMap;
	}

}
