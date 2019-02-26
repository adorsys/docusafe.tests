package org.adorsys.docusafe.rest.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.adorsys.docusafe.business.types.MoveType;

import java.io.IOException;

/**
 * Created by peter on 25.02.19 16:31.
 */
public class MoveTypeAdapter extends TypeAdapter<MoveType> {
    @Override
    public void write(JsonWriter out, MoveType moveType) throws IOException {
        out.value(moveType.toString());
    }

    @Override
    public MoveType read(JsonReader in) throws IOException {
        return MoveType.valueOf(in.nextString());
    }
}