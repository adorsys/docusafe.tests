package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.connection.FilesystemRootBucketName;

import java.io.IOException;

public class FilesystemRootBucketNameJsonAdapter extends TypeAdapter<FilesystemRootBucketName> {
    @Override
    public void write(JsonWriter out, FilesystemRootBucketName filesystemRootBucketName) throws IOException {
        out.value(filesystemRootBucketName.getValue());
    }

    @Override
    public FilesystemRootBucketName read(JsonReader in) throws IOException {
        return new FilesystemRootBucketName(in.nextString());
    }
}

