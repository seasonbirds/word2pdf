package com.example.word2pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Word转PDF应用启动类
 * 基于Spring Boot 3.2.5，适配Java 21
 */
@SpringBootApplication
public class Word2PdfApplication {

    public static void main(String[] args) {
        SpringApplication.run(Word2PdfApplication.class, args);
        System.out.println("========================================");
        System.out.println("  Word转PDF服务已启动");
        System.out.println("  服务地址: http://localhost:8080");
        System.out.println("  健康检查: http://localhost:8080/api/v1/convert/health");
        System.out.println("  API文档: http://localhost:8080/api/v1/convert/word-to-pdf (POST)");
        System.out.println("========================================");
    }
}
