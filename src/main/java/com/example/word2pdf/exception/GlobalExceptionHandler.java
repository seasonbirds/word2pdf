package com.example.word2pdf.exception;

import com.example.word2pdf.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 * 统一处理应用中的异常，返回标准化的响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理文件转换异常
     *
     * @param ex 文件转换异常
     * @return 标准化响应
     */
    @ExceptionHandler(FileConversionException.class)
    public ResponseEntity<ApiResponse<String>> handleFileConversionException(FileConversionException ex) {
        log.error("文件转换异常: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error(500, "文件转换失败: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理文件上传大小超出限制异常
     *
     * @param ex 文件上传大小超出限制异常
     * @return 标准化响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("文件上传大小超出限制: {}", ex.getMessage());
        ApiResponse<String> response = ApiResponse.error(400, "文件大小超过限制（最大500MB）");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理非法参数异常
     *
     * @param ex 非法参数异常
     * @return 标准化响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("非法参数异常: {}", ex.getMessage());
        ApiResponse<String> response = ApiResponse.error(400, "参数错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理其他未知异常
     *
     * @param ex 未知异常
     * @return 标准化响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("未知异常: {}", ex.getMessage(), ex);
        ApiResponse<String> response = ApiResponse.error(500, "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
