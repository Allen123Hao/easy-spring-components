package com.allen.component.common.oss;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 存储接口，用于处理文件和目录的存储操作
 */
public interface Storage {

    /**
     * 将文件内容写入指定路径。
     *
     * @param path     文件路径
     * @param contents 文件内容
     * @param config   配置信息
     * @throws IOException
     */
    void write(String path, String contents, Config config) throws IOException;

    /**
     * 将文件内容写入指定路径。
     *
     * @param path     文件路径
     * @param contents 文件内容
     * @param config   配置信息
     * @throws IOException
     */
    void writeBytes(String path, byte[] contents, Config config) throws IOException;

    /**
     * 将 FileInputStream 中的数据写入指定路径。
     *
     * @param path   文件路径
     * @param stream 数据流
     * @param config 配置信息
     * @throws IOException
     */
    void writeStream(String path, InputStream stream, Config config) throws IOException;

    /**
     * 从指定路径读取文件内容。
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws IOException
     */
    String read(String path) throws IOException;

    /**
     * 从指定路径读取文件内容。
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws IOException
     */
    byte[] readBytes(String path) throws IOException;

    /**
     * 从指定路径读取文件并返回 FileInputStream。
     *
     * @param path 文件路径
     * @return FileInputStream
     * @throws IOException
     */
    InputStream readStream(String path) throws IOException;

    /**
     * 删除指定路径的文件。
     *
     * @param path 文件路径
     * @throws IOException
     */
    void delete(String path) throws IOException;

    /**
     * 删除指定路径的目录。
     *
     * @param path 目录路径
     * @throws IOException
     */
    void deleteDirectory(String path) throws IOException;

    /**
     * 列出指定路径下的文件和目录。
     *
     * @param path      路径
     * @param recursive 是否递归遍历子目录
     * @return 目录列表
     * @throws IOException
     */
    DirectoryListing<StorageAttributes> listContents(String path, boolean recursive) throws IOException;

    /**
     * 检查文件是否存在。
     *
     * @param path 文件路径
     * @return 如果文件存在则返回true，否则返回false
     */
    boolean fileExists(String path);

    /**
     * 检查目录是否存在。
     *
     * @param path 目录路径
     * @return 如果目录存在则返回true，否则返回false
     */
    boolean directoryExists(String path);

    /**
     * 检查指定路径是否存在文件或目录。
     *
     * @param path 路径
     * @return 如果路径存在文件或目录则返回true，否则返回false
     */
    boolean has(String path);

    /**
     * 获取指定路径的最后修改时间。
     *
     * @param path 路径
     * @return 最后修改时间（以秒为单位）
     * @throws IOException
     */
    long lastModified(String path) throws IOException;

    /**
     * 获取指定路径的 MIME 类型。
     *
     * @param path 路径
     * @return MIME 类型
     * @throws IOException
     */
    String mimeType(String path) throws IOException;

    /**
     * 获取指定路径的文件大小。
     *
     * @param path 路径
     * @return 文件大小（以字节为单位）
     * @throws IOException
     */
    long fileSize(String path) throws IOException;

    /**
     * 获取指定路径的可见性。
     *
     * @param path 路径
     * @return 可见性
     */
    String visibility(String path);

    /**
     * 设置指定路径的可见性。
     *
     * @param path       路径
     * @param visibility 可见性
     */
    void setVisibility(String path, String visibility);

    /**
     * 创建指定路径的目录。
     *
     * @param path   目录路径
     * @param config 配置信息
     * @throws IOException
     */
    void createDirectory(String path, Config config) throws IOException;

    /**
     * 将文件从源路径移动到目标路径。
     *
     * @param source      源路径
     * @param destination 目标路径
     * @param config      配置信息
     * @throws IOException
     */
    void move(String source, String destination, Config config) throws IOException;

    /**
     * 将文件从源路径复制到目标路径。
     *
     * @param source      源路径
     * @param destination 目标路径
     * @param config      配置信息
     * @throws IOException
     */
    void copy(String source, String destination, Config config) throws IOException;

    /**
     * 私有 bucket 生成临时链接
     *
     * @param path      路径
     * @param expiresAt 失效时间
     * @return 临时url
     */
    String temporaryUrl(String path, LocalDateTime expiresAt);

    /**
     * 公开 bucket 生成永久链接
     *
     * @param path 路径
     * @return 公开 url
     */
    String publicUrl(String path);

    /**
     * 计算md5
     * 
     * @param path 文件路径
     * @return md5值
     * @throws IOException
     */
    String md5(String path) throws IOException;

}
