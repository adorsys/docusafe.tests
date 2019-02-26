package org.adorsys.docusafe.rest.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.adorsys.encobject.types.OverwriteFlag;

import java.io.IOException;

/**
 * Created by peter on 25.02.19 16:31.
 */
public class OverwriteFlagAdapter extends TypeAdapter<OverwriteFlag> {
    @Override
    public void write(JsonWriter out, OverwriteFlag overwriteFlag) throws IOException {
        out.value(overwriteFlag.toString());
    }

    @Override
    public OverwriteFlag read(JsonReader in) throws IOException {
        return OverwriteFlag.valueOf(in.nextString());
    }
}