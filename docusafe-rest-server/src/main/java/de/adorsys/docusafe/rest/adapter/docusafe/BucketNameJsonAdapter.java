package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.dfs.connection.api.types.BucketName;

import java.io.IOException;

/**
 * Created by peter on 13.01.18.
 */
public class BucketNameJsonAdapter extends TypeAdapter<BucketName> {
    @Override
    public void write(JsonWriter out, BucketName bucketName) throws IOException {
        out.value(bucketName.getValue());
    }
    @Override
    public BucketName read(JsonReader in) throws IOException {
        return new BucketName(in.nextString());
    }
}
