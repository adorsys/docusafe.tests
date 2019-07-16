package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3SecretKey;

import java.io.IOException;

public class AmazonS3SecretKeyJsonAdapter extends TypeAdapter<AmazonS3SecretKey> {
    @Override
    public void write(JsonWriter out, AmazonS3SecretKey amazonS3SecretKey) throws IOException {
        out.value(amazonS3SecretKey.getValue());
    }
    @Override
    public AmazonS3SecretKey read(JsonReader in) throws IOException {
        return new AmazonS3SecretKey(in.nextString());
    }
}

