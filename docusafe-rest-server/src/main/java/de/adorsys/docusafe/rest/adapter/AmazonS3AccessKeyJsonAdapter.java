package de.adorsys.docusafe.rest.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3AccessKey;

import java.io.IOException;

public class AmazonS3AccessKeyJsonAdapter extends TypeAdapter<AmazonS3AccessKey> {
    @Override
    public void write(JsonWriter out, AmazonS3AccessKey amazonS3AccessKey) throws IOException {
        out.value(amazonS3AccessKey.getValue());
    }
    @Override
    public AmazonS3AccessKey read(JsonReader in) throws IOException {
        return new AmazonS3AccessKey(in.nextString());
    }
}

