package com.example.word2pdf.exception;

/**
 * 文件转换异常
 * 当Word转PDF过程中发生错误时抛出
 */
public class FileConversionException extends RuntimeException {

    public FileConversionException(String message) {
        super(message);
    }

    public FileConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
