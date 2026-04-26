package com.example.word2pdf.service.impl;

import com.example.word2pdf.exception.FileConversionException;
import com.example.word2pdf.service.WordToPdfConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Word转PDF统一转换服务
 * 根据文件类型自动选择合适的转换器
 */
@Slf4j
@Service
public class WordToPdfConversionService {

    private final List<WordToPdfConverter> converters;

    @Autowired
    public WordToPdfConversionService(List<WordToPdfConverter> converters) {
        this.converters = converters;
    }

    /**
     * 将Word文件转换为PDF文件
     *
     * @param inputFile  输入的Word文件
     * @param outputFile 输出的PDF文件
     * @throws FileConversionException 转换失败时抛出
     */
    public void convert(File inputFile, File outputFile) {
        String fileName = inputFile.getName();
        log.info("开始转换文件: {}", fileName);
        
        // 查找合适的转换器
        WordToPdfConverter converter = findConverter(fileName);
        if (converter == null) {
            throw new FileConversionException("不支持的文件格式: " + fileName);
        }
        
        try {
            converter.convert(inputFile, outputFile);
            log.info("文件转换完成: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件转换失败: {}", fileName, e);
            throw new FileConversionException("文件转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将Word输入流转换为PDF输出流
     *
     * @param inputStream  输入的Word流
     * @param outputStream 输出的PDF流
     * @param fileName     文件名（用于确定文件类型）
     * @throws FileConversionException 转换失败时抛出
     */
    public void convert(InputStream inputStream, OutputStream outputStream, String fileName) {
        log.info("开始转换流: {}", fileName);
        
        // 查找合适的转换器
        WordToPdfConverter converter = findConverter(fileName);
        if (converter == null) {
            throw new FileConversionException("不支持的文件格式: " + fileName);
        }
        
        try {
            converter.convert(inputStream, outputStream);
            log.info("流转换完成: {}", fileName);
        } catch (IOException e) {
            log.error("流转换失败: {}", fileName, e);
            throw new FileConversionException("流转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查找支持指定文件的转换器
     *
     * @param fileName 文件名
     * @return 支持的转换器，如果没有则返回null
     */
    private WordToPdfConverter findConverter(String fileName) {
        for (WordToPdfConverter converter : converters) {
            if (converter.supports(fileName)) {
                return converter;
            }
        }
        return null;
    }

    /**
     * 检查是否支持指定的文件格式
     *
     * @param fileName 文件名
     * @return 如果支持则返回true，否则返回false
     */
    public boolean supports(String fileName) {
        return findConverter(fileName) != null;
    }
}
