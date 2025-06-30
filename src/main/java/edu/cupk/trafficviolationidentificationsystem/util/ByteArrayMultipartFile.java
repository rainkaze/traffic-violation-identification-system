package edu.cupk.trafficviolationidentificationsystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 一个 {@link MultipartFile} 接口的内存实现。
 * <p>
 * 此类用于将字节数组 ({@code byte[]}) 包装成 Spring 的 MultipartFile 对象。
 * 这在处理非标准 HTTP 文件上传的场景中非常有用，例如：
 * <ul>
 * <li>从消息队列（如 RabbitMQ）接收文件内容。</li>
 * <li>在单元测试中模拟文件上传。</li>
 * <li>从数据库或其他来源读取文件字节后，将其传递给需要 MultipartFile 的服务。</li>
 * </ul>
 * </p>
 */
public class ByteArrayMultipartFile implements MultipartFile {

    private static final Logger log = LoggerFactory.getLogger(ByteArrayMultipartFile.class);

    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    /**
     * 构造一个新的 ByteArrayMultipartFile。
     *
     * @param content          文件的完整字节内容。
     * @param name             在表单中该文件输入字段的名称 (e.g., "file").
     * @param originalFilename 文件的原始名称，包含扩展名 (e.g., "image.jpg").
     * @param contentType      文件的MIME类型 (e.g., "image/jpeg").
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

    /**
     * 将文件内容写入到指定的 {@link File} 目标。
     *
     * @param dest 目标文件。
     * @throws IOException 如果写入过程中发生 I/O 错误。
     * @throws IllegalStateException 如果无法写入。
     */
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        log.info("正在将内存文件 '{}' ({} bytes) 传输到磁盘路径: {}", this.originalFilename, this.content.length, dest.getAbsolutePath());
        Files.write(dest.toPath(), this.content);
        log.info("文件成功传输到: {}", dest.getAbsolutePath());
    }

    /**
     * 将文件内容写入到指定的 {@link Path} 目标 (NIO aPI)。
     *
     * @param dest 目标路径。
     * @throws IOException 如果写入过程中发生 I/O 错误。
     * @throws IllegalStateException 如果无法写入。
     */
    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        log.info("正在将内存文件 '{}' ({} bytes) 传输到磁盘路径: {}", this.originalFilename, this.content.length, dest.toAbsolutePath());
        Files.write(dest, this.content);
        log.info("文件成功传输到: {}", dest.toAbsolutePath());
    }
}