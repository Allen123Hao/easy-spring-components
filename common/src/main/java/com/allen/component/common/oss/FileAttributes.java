package com.allen.component.common.oss;

import java.util.Map;

/**
 * FileAttributes
 */
public class FileAttributes implements StorageAttributes {
    private String path;
    private final Long fileSize;
    private final String visibility;
    private final Long lastModified;
    private final String mimeType;
    private final Map<String, String> extraMetadata;

    public FileAttributes(String path, Long fileSize, String visibility, Long lastModified, String mimeType, Map<String, String> extraMetadata) {
        this.path = path;
        this.fileSize = fileSize;
        this.visibility = visibility;
        this.lastModified = lastModified;
        this.mimeType = mimeType;
        this.extraMetadata = extraMetadata;

        this.path = this.path.startsWith("/") ? this.path.substring(1) : this.path;
    }

    @Override
    public String type() {
        return StorageAttributes.TYPE_FILE;
    }

    @Override
    public String path() {
        return path;
    }

    public Long fileSize() {
        return fileSize;
    }

    @Override
    public String visibility() {
        return visibility;
    }

    @Override
    public Long lastModified() {
        return lastModified;
    }

    public String mimeType() {
        return mimeType;
    }

    @Override
    public Map<String, String> extraMetadata() {
        return extraMetadata;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public FileAttributes withPath(String path) {
        return new FileAttributes(path, fileSize, visibility, lastModified, mimeType, extraMetadata);
    }

}
