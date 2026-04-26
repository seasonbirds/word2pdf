package com.example.word2pdf.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件存储服务接口
 * 负责文件的存储、获取和清理等操作
 */
public interface FileStorageService {

    /**
     * 存储上传的文件
     *
     * @param file 上传的MultipartFile文件
     * @return 存储后的文件对象
     * @throws IOException 存储过程中发生的IO异常
     */
    File storeFile(MultipartFile file) throws IOException;

    /**
     * 生成输出PDF文件路径
     *
     * @param inputFileName 输入文件名
     * @return 输出PDF文件对象
     */
    File generateOutputFile(String inputFileName);

    /**
     * 获取临时文件存储目录
     *
     * @return 临时目录路径
     */
    String getTempDirectory();

    /**
     * 清理临时文件
     *
     * @param files 要清理的文件数组
     */
    void cleanupFiles(File... files);

    /**
     * 检查文件大小是否符合要求
     *
     * @param fileSize 文件大小（字节）
     * @return 如果符合要求则返回true，否则返回false
     */
    boolean isValidFileSize(long fileSize);

    /**
     * 检查文件类型是否支持
     *
     * @param fileName 文件名
     * @return 如果支持则返回true，否则返回false
     */
    boolean isValidFileType(String fileName);
}
