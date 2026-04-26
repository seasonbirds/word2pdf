package com.example.word2pdf.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Word转PDF转换器接口
 * 定义Word文件转换为PDF文件的标准方法
 */
public interface WordToPdfConverter {

    /**
     * 将Word文件转换为PDF文件
     *
     * @param inputFile  输入的Word文件
     * @param outputFile 输出的PDF文件
     * @throws IOException 转换过程中发生的IO异常
     */
    void convert(File inputFile, File outputFile) throws IOException;

    /**
     * 将Word输入流转换为PDF输出流
     *
     * @param inputStream  输入的Word流
     * @param outputStream 输出的PDF流
     * @throws IOException 转换过程中发生的IO异常
     */
    void convert(InputStream inputStream, OutputStream outputStream) throws IOException;

    /**
     * 检查转换器是否支持指定的文件格式
     *
     * @param fileName 文件名
     * @return 如果支持则返回true，否则返回false
     */
    boolean supports(String fileName);
}
