package com.allen.component.common.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云OSS存储实现类。
 * 该类实现了 {@link Storage} 接口，提供了与阿里云对象存储服务（OSS）交互的方法。
 */
@Slf4j
public class AliyunStorage implements Storage {

    private final String publicUrl;

    private final OSS ossClient;

    private final String bucketName;

    private static final String DEFAULT_MIME = "application/octet-stream";

    /**
     * 构造一个新的 AliyunStorage 实例。
     *
     * @param endpoint     OSS endpoint
     * @param accessKey    访问密钥ID
     * @param accessSecret 访问密钥密码
     * @param bucketName   存储桶名称
     */
    public AliyunStorage(String endpoint, String accessKey, String accessSecret, String bucketName) {
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, accessSecret);
        this.bucketName = bucketName;
        this.publicUrl = null;
    }

    public AliyunStorage(String endpoint, String accessKey, String accessSecret, String bucketName, String publicUrl) {
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, accessSecret);
        this.bucketName = bucketName;
        this.publicUrl = publicUrl;
    }

    public AliyunStorage(OSS ossClient, String bucketName) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.publicUrl = null;
    }

    public AliyunStorage(OSS ossClient, String bucketName, String publicUrl) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.publicUrl = publicUrl;
    }

    /**
     * 将字符串内容写入指定路径。
     *
     * @param path     文件路径
     * @param contents 要写入的字符串内容
     * @param config   配置选项
     */
    @Override
    public void write(String path, String contents, Config config) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(config.get("mime", DEFAULT_MIME));
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(contents.getBytes()), metadata);
    }

    /**
     * 将字节数组内容写入指定路径。
     *
     * @param path     文件路径
     * @param contents 要写入的字节数组内容
     * @param config   配置选项
     */
    @Override
    public void writeBytes(String path, byte[] contents, Config config) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(config.get("mime", DEFAULT_MIME));

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(contents), metadata);
    }

    /**
     * 将输入流内容写入指定路径。
     *
     * @param path   文件路径
     * @param stream 要写入的输入流
     * @param config 配置选项
     */
    @Override
    public void writeStream(String path, InputStream stream, Config config) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(config.get("mime", DEFAULT_MIME));

        ossClient.putObject(bucketName, path, stream, metadata);
    }

    /**
     * 读取指定路径的文件内容。
     *
     * @param path 文件路径
     * @return 文件内容的字符串表示，如果文件不存在则返回 null
     */
    @Override
    public String read(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        OSSObject object = ossClient.getObject(bucketName, path);
        return getStringContent(object.getObjectContent());
    }

    /**
     * 读取指定路径的文件内容为字节数组。
     *
     * @param path 文件路径
     * @return 文件内容的字节数组表示，如果文件不存在则返回空字节数组
     */
    @Override
    public byte[] readBytes(String path) {
        if (StringUtils.isBlank(path)) {
            return new byte[0];
        }
        OSSObject object = ossClient.getObject(bucketName, path);
        return getBytesContent(object.getObjectContent());
    }

    /**
     * 读取指定路径的文件内容为输入流。
     *
     * @param path 文件路径
     * @return 文件内容的输入流，如果文件不存在则返回 null
     */
    @Override
    public InputStream readStream(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        try {
            OSSObject object = ossClient.getObject(bucketName, path);
            return object.getObjectContent();
        } catch (OSSException | ClientException e) {
            log.error("readStream:", e);
        }
        return null;
    }

    /**
     * 删除指定路径的文件。
     *
     * @param path 要删除的文件路径
     */
    @Override
    public void delete(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ossClient.deleteObject(bucketName, path);
    }

    /**
     * 删除指定路径的目录及其所有内容。
     *
     * @param path 要删除的目录路径
     */
    @Override
    public void deleteDirectory(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        int maxKeys = 200;
        String nextContinueToken = null;
        ListObjectsV2Result result = null;
        do {
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request(bucketName)
                    .withPrefix(path)
                    .withMaxKeys(maxKeys)
                    .withContinuationToken(nextContinueToken);

            result = ossClient.listObjectsV2(listObjectsRequest);

            if (CollectionUtils.isNotEmpty(result.getObjectSummaries())) {
                List<String> keys = new ArrayList<>();
                for (OSSObjectSummary summary : result.getObjectSummaries()) {
                    keys.add(summary.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys)
                        .withEncodingType("url");
                ossClient.deleteObjects(deleteObjectsRequest);
            }

            nextContinueToken = result.getNextContinuationToken();
        } while (result.isTruncated());
    }

    /**
     * 列出指定路径下的内容。
     *
     * @param path      要列出内容的路径
     * @param recursive 是否递归列出子目录内容
     * @return 包含文件和目录属性的 {@link DirectoryListing} 对象
     */
    @Override
    public DirectoryListing<StorageAttributes> listContents(String path, boolean recursive) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String nextMarker = null;
        List<StorageAttributes> attributesList = new ArrayList<>();
        do {
            ListObjectsRequest listRequest = new ListObjectsRequest(bucketName).withPrefix(path).withMarker(nextMarker);
            ObjectListing objectListing = ossClient.listObjects(listRequest);
            attributesList.addAll(listFilesAttributes(path, recursive, objectListing));
            attributesList.addAll(listDirectoriesAttributes(path, recursive, objectListing));
            nextMarker = objectListing.getNextMarker();
        } while (nextMarker != null);
        return new DirectoryListing<>(attributesList);
    }

    private List<DirectoryAttributes> listDirectoriesAttributes(String path, boolean recursive,
            ObjectListing objectListing) {
        List<DirectoryAttributes> directoryAttributes = new ArrayList<>();
        List<String> directories = objectListing.getCommonPrefixes();
        for (String directory : directories) {
            String dirPath = directory.endsWith("/") ? directory.substring(0, directory.length() - 1) : directory;
            if (!recursive && !isSubdirectory(path, dirPath)) {
                // todo 不同批次获取的时候，目录公共前缀会重复
                continue; // 跳过非子一级的目录
            }
            DirectoryAttributes attributes = createDirectoryAttributes(directory);
            directoryAttributes.add(attributes);
        }
        return directoryAttributes;
    }

    private List<FileAttributes> listFilesAttributes(String path, boolean recursive, ObjectListing objectListing) {
        List<OSSObjectSummary> objects = objectListing.getObjectSummaries();
        List<FileAttributes> filesAttributes = new ArrayList<>();
        for (OSSObjectSummary object : objects) {
            if (!recursive) {
                String objDir = object.getKey().substring(0, object.getKey().lastIndexOf("/"));
                String pathDir = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                if (!objDir.equals(pathDir)) {
                    // 跳过深级的目录和文件
                    continue;
                }
            }
            FileAttributes attributes = createFileAttributes(object);
            filesAttributes.add(attributes);
        }
        return filesAttributes;
    }

    /**
     * 检查指定路径的文件是否存在。
     *
     * @param path 要检查的文件路径
     * @return 如果文件存在返回 true，否则返回 false
     */
    @Override
    public boolean fileExists(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        return ossClient.doesObjectExist(bucketName, path);
    }

    /**
     * 检查指定路径的目录是否存在。
     *
     * @param path 要检查的目录路径
     * @return 如果目录存在返回 true，否则返回 false
     */
    @Override
    public boolean directoryExists(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }

        ListObjectsRequest listRequest = new ListObjectsRequest(bucketName).withPrefix(path);
        ObjectListing objectListing = ossClient.listObjects(listRequest);

        return !objectListing.getCommonPrefixes().isEmpty();
    }

    /**
     * 检查指定路径的文件或目录是否存在。
     *
     * @param path 要检查的路径
     * @return 如果文件或目录存在返回 true，否则返回 false
     */
    @Override
    public boolean has(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        return fileExists(path) || directoryExists(path);
    }

    /**
     * 获取指定路径文件的最后修改时间。
     *
     * @param path 文件路径
     * @return 文件的最后修改时间（Unix 时间戳），如果文件不存在则返回 -1
     */
    @Override
    public long lastModified(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }
        GenericRequest genericRequest = new GenericRequest(bucketName, path);
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(genericRequest);
        return (int) (objectMetadata.getLastModified().getTime() / 1000);
    }

    /**
     * 获取指定路径文件的 MIME 类型。
     *
     * @param path 文件路径
     * @return 文件的 MIME 类型，如果文件不存在则返回 null
     */
    @Override
    public String mimeType(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, path);
        return objectMetadata.getContentType();
    }

    /**
     * 获取指定路径文件的大小。
     *
     * @param path 文件路径
     * @return 文件的大小（字节数），如果文件不存在则返回 -1
     */
    @Override
    public long fileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, path);
        return (int) objectMetadata.getContentLength();
    }

    /**
     * 获取指定路径文件的可见性。
     *
     * @param path 文件路径
     * @return 文件的可见性设置，如果文件不存在则返回 null
     */
    @Override
    public String visibility(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        ObjectAcl objectAcl = ossClient.getObjectAcl(bucketName, path);
        return objectAcl.getPermission().toString();
    }

    /**
     * 设置指定路径文件的可见性。
     *
     * @param path       文件路径
     * @param visibility 要设置的可见性
     */
    @Override
    public void setVisibility(String path, String visibility) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        CannedAccessControlList acl = CannedAccessControlList.valueOf(visibility);
        ossClient.setObjectAcl(bucketName, path, acl);
    }

    @Override
    public void createDirectory(String path, Config config) {
        // Aliyun OSS does not have a concept of directories. Directories are
        // represented by prefixes in object names.
        // Therefore, creating a "directory" is not necessary.
        throw new UnsupportedOperationException();
    }

    /**
     * 移动文件或目录。
     *
     * @param source      源路径
     * @param destination 目标路径
     * @param config      配置选项
     */
    @Override
    public void move(String source, String destination, Config config) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(destination)) {
            return;
        }
        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, source, bucketName, destination);
        CopyObjectResult copyResult = ossClient.copyObject(copyRequest);

        if (copyResult.getETag() != null) {
            ossClient.deleteObject(bucketName, source);
        }
    }

    /**
     * 复制文件或目录。
     *
     * @param source      源路径
     * @param destination 目标路径
     * @param config      配置选项
     */
    @Override
    public void copy(String source, String destination, Config config) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(destination)) {
            return;
        }
        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, source, bucketName, destination);
        ossClient.copyObject(copyRequest);
    }

    @Override
    public String temporaryUrl(String path, LocalDateTime expiresAt) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取文件的公共URL。
     *
     * @param path 文件路径
     * @return 文件的公共URL，如果未配置公共URL或路径为空则返回 null
     */
    @Override
    public String publicUrl(String path) {
        if (StringUtils.isBlank(publicUrl) || StringUtils.isBlank(path)) {
            return null;
        }
        if (path.matches("^(http|https)://.*$")) {
            return path;
        }
        return StringUtils.removeEnd(publicUrl, "/") + "/" + StringUtils.removeStart(path, "/");
    }

    /**
     * 获取指定路径文件的 MD5 哈希值。
     *
     * @param path 文件路径
     * @return 文件的 MD5 哈希值，如果文件不存在则返回 null
     */
    @Override
    public String md5(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, path);
        return objectMetadata.getContentMD5();
    }

    // Helper methods

    /**
     * 从输入流中读取内容并转换为字符串。
     *
     * @param content 包含内容的输入流
     * @return 读取的字符串内容，如果发生错误则返回 null
     */
    private String getStringContent(InputStream content) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("getStringContent:", e);
            return null;
        }
    }

    private byte[] getBytesContent(InputStream content) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("getBytesContent:", e);
            return new byte[0];
        }
    }

    private DirectoryAttributes createDirectoryAttributes(String path) {
        return new DirectoryAttributes(path);
    }

    private FileAttributes createFileAttributes(OSSObjectSummary object) {
        Long lastModified = object.getLastModified().getTime() / 1000;
        return new FileAttributes(object.getKey(), object.getSize(), null, lastModified, null, null);
    }

    private boolean isSubdirectory(String basePath, String targetPath) {
        return targetPath.startsWith(basePath) && !targetPath.substring(basePath.length()).contains("/");
    }

}
