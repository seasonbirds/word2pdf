package com.example.word2pdf.service.impl;

import com.example.word2pdf.service.FileStorageService;
import com.example.word2pdf.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储服务实现类
 * 负责文件的存储、获取和清理等操作
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String tempDirectory;
    private final long maxFileSize;

    public FileStorageServiceImpl(
            @Value("${file.upload.temp-dir:${java.io.tmpdir}/word2pdf}") String tempDirectory,
            @Value("${file.upload.max-size:524288000}") long maxFileSize) throws IOException {
        this.tempDirectory = tempDirectory;
        this.maxFileSize = maxFileSize;
        // 确保临时目录存在
        FileUtils.ensureDirectoryExists(tempDirectory);
        log.info("文件存储服务初始化完成，临时目录: {}, 最大文件大小: {} bytes", tempDirectory, maxFileSize);
    }

    @Override
    public File storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("文件名不能为空");
        }

        // 生成唯一文件名
        String uniqueFileName = FileUtils.generateUniqueFileName(originalFilename);
        Path targetPath = Paths.get(tempDirectory, uniqueFileName);
        
        log.info("存储上传文件: {} -> {}", originalFilename, targetPath);
        
        // 保存文件
        Files.copy(file.getInputStream(), targetPath);
        
        return targetPath.toFile();
    }

    @Override
    public File generateOutputFile(String inputFileName) {
        String baseName = FileUtils.getFileNameWithoutExtension(inputFileName);
        String outputFileName = baseName + ".pdf";
        // 为了避免重名，添加时间戳
        String uniqueOutputFileName = FileUtils.generateUniqueFileName(outputFileName);
        Path outputPath = Paths.get(tempDirectory, uniqueOutputFileName);
        log.info("生成输出文件路径: {}", outputPath);
        return outputPath.toFile();
    }

    @Override
    public String getTempDirectory() {
        return tempDirectory;
    }

    @Override
    public void cleanupFiles(File... files) {
        if (files != null && files.length > 0) {
            FileUtils.deleteFiles(files);
        }
    }

    @Override
    public boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    @Override
    public boolean isValidFileType(String fileName) {
        return FileUtils.isWordDocument(fileName);
    }
}
