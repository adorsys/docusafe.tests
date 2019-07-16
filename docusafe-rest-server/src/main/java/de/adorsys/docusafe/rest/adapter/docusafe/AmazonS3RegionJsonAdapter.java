package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3Region;

import java.io.IOException;

public class AmazonS3RegionJsonAdapter extends TypeAdapter<AmazonS3Region> {
    @Override
    public void write(JsonWriter out, AmazonS3Region amazonS3Region) throws IOException {
        out.value(amazonS3Region.getValue());
    }
    @Override
    public AmazonS3Region read(JsonReader in) throws IOException {
        return new AmazonS3Region(in.nextString());
    }
}

