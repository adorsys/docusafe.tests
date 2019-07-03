package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3RootBucketName;

import java.io.IOException;

public class AmazonS3RootBucketNameJsonAdapter extends TypeAdapter<AmazonS3RootBucketName> {
    @Override
    public void write(JsonWriter out, AmazonS3RootBucketName amazonS3RootBucketName) throws IOException {
        out.value(amazonS3RootBucketName.getValue());
    }
    @Override
    public AmazonS3RootBucketName read(JsonReader in) throws IOException {
        return new AmazonS3RootBucketName(in.nextString());
    }
}

