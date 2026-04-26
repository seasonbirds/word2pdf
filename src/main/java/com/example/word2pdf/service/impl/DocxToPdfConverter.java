package com.example.word2pdf.service.impl;

import com.example.word2pdf.service.WordToPdfConverter;
import com.example.word2pdf.util.FileUtils;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Docx格式转PDF转换器
 * 支持.docx格式的Word文档转换为PDF
 */
@Slf4j
@Service
public class DocxToPdfConverter implements WordToPdfConverter {

    @Override
    public void convert(File inputFile, File outputFile) throws IOException {
        log.info("开始转换Docx文件: {} -> {}", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            convert(inputStream, outputStream);
        }
        
        log.info("Docx文件转换完成: {}", outputFile.getAbsolutePath());
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            PdfOptions options = PdfOptions.create();
            // 执行转换
            PdfConverter.getInstance().convert(document, outputStream, options);
        }
    }

    @Override
    public boolean supports(String fileName) {
        return FileUtils.isDocxFile(fileName);
    }
}
