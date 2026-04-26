package com.example.word2pdf.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 * 提供文件处理的常用工具方法
 */
@Slf4j
@UtilityClass
public class FileUtils {

    /**
     * 获取文件扩展名（不带点）
     *
     * @param fileName 文件名
     * @return 文件扩展名，全小写
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String baseName = getFileNameWithoutExtension(originalName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return baseName + "_" + timestamp + "." + extension;
    }

    /**
     * 确保目录存在
     *
     * @param directoryPath 目录路径
     * @return 目录对象
     * @throws IOException 创建目录失败时抛出
     */
    public static File ensureDirectoryExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("创建目录: {}", directoryPath);
        }
        return path.toFile();
    }

    /**
     * 清理文件
     *
     * @param files 要删除的文件
     */
    public static void deleteFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                try {
                    Files.delete(file.toPath());
                    log.debug("删除文件: {}", file.getAbsolutePath());
                } catch (IOException e) {
                    log.warn("删除文件失败: {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * 检查文件是否为Word文档
     *
     * @param fileName 文件名
     * @return 如果是Word文档则返回true，否则返回false
     */
    public static boolean isWordDocument(String fileName) {
        String extension = getFileExtension(fileName);
        return "doc".equals(extension) || "docx".equals(extension);
    }

    /**
     * 检查文件是否为.docx格式
     *
     * @param fileName 文件名
     * @return 如果是.docx格式则返回true，否则返回false
     */
    public static boolean isDocxFile(String fileName) {
        return "docx".equals(getFileExtension(fileName));
    }

    /**
     * 检查文件是否为.doc格式
     *
     * @param fileName 文件名
     * @return 如果是.doc格式则返回true，否则返回false
     */
    public static boolean isDocFile(String fileName) {
        return "doc".equals(getFileExtension(fileName));
    }
}
