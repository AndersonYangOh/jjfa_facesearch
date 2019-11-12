# 基于创新业务的人脸搜索系统
## 一、功能介绍
* 人脸查找是从人脸库中寻找到与待识别人脸相似的一张脸或者多张脸。
* 一般是提取出待识别人脸的特征码，在通过特征码与人脸库中的进行比对。
## 二、采取架构形式
* spring+springMVC+mybatis+mysql；

## 三、所需依赖
`<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"> <modelVersion>4.0.0</modelVersion><groupId>com.tt</groupId> <artifactId>face</artifactId><version>0.0.1-SNAPSHOT</version><packaging>war</packaging>
  
 	 <parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.1.RELEASE</version>
	</parent>
  
    <properties>
        <spring.version>5.0.5.RELEASE</spring.version>
        <log4j.version>2.8.2</log4j.version>
        <slf4j.version>1.7.25</slf4j.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        
        <!-- swagger -->
        <dependency>
			<groupId>com.github.xiaoymin</groupId>
			<artifactId>knife4j-spring-boot-starter</artifactId>
			<version>1.9.6</version>
		</dependency>
                 
        <!-- log4j2 -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        			        
		<dependency> <!-- 引入log4j2依赖 -->  
			   <groupId>org.springframework.boot</groupId>  
			   <artifactId>spring-boot-starter-log4j2</artifactId>  
		</dependency>
        
        
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        
        <!-- jsp支持开启 -->  
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
        </dependency>
        <!-- servlet支持开启 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
        </dependency>
        
        <!--  log4j to slf4j bridge -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j-impl</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
            <version>2.4.2</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13-beta-1</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
		  <groupId>commons-fileupload</groupId>
		  <artifactId>commons-fileupload</artifactId>
		  <version>1.4</version>
		</dependency>
        
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
        
        <dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpclient</artifactId>
		    <version>4.5.3</version>
		</dependency>
		
		<dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpmime</artifactId>
		    <version>4.5.3</version>
		</dependency>
		
		  <dependency>
	      <groupId>com.lmax</groupId>
	      <artifactId>disruptor</artifactId>
	      <version>3.4.2</version>
	    </dependency>
	    
	    <dependency>
           <groupId>org.xerial</groupId>
           <artifactId>sqlite-jdbc</artifactId>
           <version>3.25.2</version>
       </dependency>
       <dependency>
           <groupId>org.apache.commons</groupId>
           <artifactId>commons-pool2</artifactId>
           <version>2.4.2</version>
       </dependency>
       
       <!--简化代码的工具包-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!--mybatis-plus的springboot支持-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.0</version>
        </dependency>
        <!--mysql驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.10</version>
        </dependency>
        
		<dependency>  
		    <groupId>com.alibaba</groupId>  
		    <artifactId>fastjson</artifactId>  
		    <version>1.2.41</version>  
		</dependency>
        
        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<exclusions> 
				<exclusion> 
					<groupId>org.springframework.boot</groupId> 
					<artifactId>spring-boot-starter-logging</artifactId> 
				</exclusion> 
				<exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
			</exclusions> 
		</dependency>
		 <!--@ConfiguaritionProperties的执行器的配置-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}-${project.version}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                 <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
        <!--     <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
            </plugin> -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
               	    <warName>ttface</warName>
                    <webResources>
                        <resource>
                            <directory>src/main/webapp/WEB-INF/lib</directory>
                            <targetPath>WEB-INF/lib/</targetPath>
                            <includes>
                                <include>**/*.jar</include>
                            </includes>
                        </resource>
                    </webResources>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>`


