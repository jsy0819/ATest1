package com.example.demo.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

//Spring MVC 설정 클래스 (파일 업로드 기능 포함)
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 시스템 임시 디렉토리 기본값 사용, 없으면 fallback
    @Value("${file.upload-dir:${java.io.tmpdir}/app-uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 디렉토리를 절대 경로로 변환
        File uploadDirectory = new File(uploadDir).getAbsoluteFile();
        
        // 디렉토리가 없으면 생성
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        
        // file:// 프로토콜 사용하여 파일 시스템 경로 지정
        String resourceLocation = "file:" + uploadDirectory.getAbsolutePath() + File.separator;
        
        System.out.println("=== 정적 리소스 매핑 ===");
        System.out.println("URL 패턴: /uploaded-images/**");
        System.out.println("실제 경로: " + resourceLocation);
        
        // URL 패턴 '/uploaded-images/**'로 요청이 들어오면 실제 업로드 경로로 매핑
        registry.addResourceHandler("/uploaded-images/**")
                .addResourceLocations(resourceLocation);
    }
}