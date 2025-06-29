package edu.cupk.trafficviolationidentificationsystem.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 一个MultipartFile接口的自定义实现，用于将字节数组（byte[]）包装成文件对象。
 * 这在从消息队列等非HTTP请求来源接收文件内容时非常有用。
 */
public class ByteArrayMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    /**
     * 构造函数。
     *
     * @param content          文件的字节内容。
     * @param name             表单字段的名称。
     * @param originalFilename 原始文件名。
     * @param contentType      文件的MIME类型。
     */
    public ByteArrayMultipartFile(byte[] content, String name, String originalFilename, String contentType) {
        this.content = content;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content == null || this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), this.content);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.write(dest, this.content);
    }
}