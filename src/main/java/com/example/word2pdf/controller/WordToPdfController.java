package com.example.word2pdf.controller;

import com.example.word2pdf.dto.ApiResponse;
import com.example.word2pdf.exception.FileConversionException;
import com.example.word2pdf.service.FileStorageService;
import com.example.word2pdf.service.impl.WordToPdfConversionService;
import com.example.word2pdf.util.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Word转PDF控制器
 * 提供文件上传、转换和下载的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/convert")
public class WordToPdfController {

    private final FileStorageService fileStorageService;
    private final WordToPdfConversionService conversionService;

    @Autowired
    public WordToPdfController(FileStorageService fileStorageService, 
                               WordToPdfConversionService conversionService) {
        this.fileStorageService = fileStorageService;
        this.conversionService = conversionService;
    }

    /**
     * 上传Word文件并转换为PDF
     *
     * @param file 上传的Word文件
     * @return 转换结果，包含下载链接
     */
    @PostMapping("/word-to-pdf")
    public ApiResponse<Map<String, Object>> convertWordToPdf(
            @RequestParam("file") MultipartFile file) {
        
        log.info("收到文件转换请求: 文件名={}, 大小={} bytes", 
                file.getOriginalFilename(), file.getSize());
        
        // 验证文件
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return ApiResponse.error(400, "文件名不能为空");
        }
        
        // 验证文件类型
        if (!fileStorageService.isValidFileType(originalFilename)) {
            return ApiResponse.error(400, "不支持的文件格式，请上传.doc或.docx文件");
        }
        
        // 验证文件大小
        if (!fileStorageService.isValidFileSize(file.getSize())) {
            return ApiResponse.error(400, "文件大小超过限制（最大500MB）");
        }
        
        File inputFile = null;
        File outputFile = null;
        
        try {
            // 存储上传的文件
            inputFile = fileStorageService.storeFile(file);
            log.info("文件存储成功: {}", inputFile.getAbsolutePath());
            
            // 生成输出文件路径
            outputFile = fileStorageService.generateOutputFile(originalFilename);
            
            // 执行转换
            conversionService.convert(inputFile, outputFile);
            log.info("文件转换成功: {}", outputFile.getAbsolutePath());
            
            // 准备响应数据
            Map<String, Object> result = new HashMap<>();
            String outputFileName = FileUtils.getFileNameWithoutExtension(originalFilename) + ".pdf";
            result.put("originalFileName", originalFilename);
            result.put("outputFileName", outputFileName);
            result.put("fileSize", outputFile.length());
            result.put("downloadUrl", "/api/v1/convert/download/" + outputFile.getName());
            
            return ApiResponse.success("文件转换成功", result);
            
        } catch (FileConversionException e) {
            log.error("文件转换失败", e);
            return ApiResponse.error(500, "文件转换失败: " + e.getMessage());
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return ApiResponse.error(500, "文件处理失败: " + e.getMessage());
        } finally {
            // 清理输入文件，输出文件保留供下载
            if (inputFile != null && inputFile.exists()) {
                fileStorageService.cleanupFiles(inputFile);
            }
        }
    }

    /**
     * 下载转换后的PDF文件
     *
     * @param fileName 文件名
     * @param response HTTP响应
     */
    @GetMapping("/download/{fileName}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
        log.info("收到文件下载请求: {}", fileName);
        
        File tempDir = new File(fileStorageService.getTempDirectory());
        File file = new File(tempDir, fileName);
        
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            // 设置响应头
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename*=UTF-8''" + encodedFileName);
            response.setContentLengthLong(file.length());
            
            // 写入响应流
            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            log.info("文件下载成功: {}", fileName);
            
        } catch (IOException e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 下载完成后清理文件
            if (file.exists()) {
                fileStorageService.cleanupFiles(file);
            }
        }
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("服务运行正常", "OK");
    }
}
