package com.allen.component.common.oss;

import java.util.Map;

/**
 * DirectoryAttributes
 */
public class DirectoryAttributes implements StorageAttributes {
    private final String path;
    private final String visibility;
    private final Long lastModified;
    private final Map<String, String> extraMetadata;

    public DirectoryAttributes(String path) {
        this.path = path.trim().replaceAll("^/|/$", "");
        this.visibility = null;
        this.lastModified = null;
        this.extraMetadata = null;
    }

    public DirectoryAttributes(String path, String visibility, Long lastModified, Map<String, String> extraMetadata) {
        this.visibility = visibility;
        this.lastModified = lastModified;
        this.extraMetadata = extraMetadata;
        this.path = path.trim().replaceAll("^/|/$", "");
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String type() {
        return StorageAttributes.TYPE_DIRECTORY;
    }

    @Override
    public String visibility() {
        return visibility;
    }

    @Override
    public Long lastModified() {
        return lastModified;
    }

    @Override
    public Map<String, String> extraMetadata() {
        return extraMetadata;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public DirectoryAttributes withPath(String path) {
        return new DirectoryAttributes(path, visibility, lastModified, extraMetadata);
    }

}
