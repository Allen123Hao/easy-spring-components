package com.allen.component.common.oss;

import java.util.Map;


public interface StorageAttributes {

    String ATTRIBUTE_PATH = "path";
    String ATTRIBUTE_TYPE = "type";
    String ATTRIBUTE_FILE_SIZE = "file_size";
    String ATTRIBUTE_VISIBILITY = "visibility";
    String ATTRIBUTE_LAST_MODIFIED = "last_modified";
    String ATTRIBUTE_MIME_TYPE = "mime_type";
    String ATTRIBUTE_EXTRA_METADATA = "extra_metadata";

    String TYPE_FILE = "file";
    String TYPE_DIRECTORY = "dir";

    String path();

    String type();

    String visibility();

    Long lastModified();

    boolean isFile();

    boolean isDir();

    StorageAttributes withPath(String path);

    Map<String, String> extraMetadata();

}
