package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;

import java.io.IOException;

/**
 * Created by peter on 22.01.2018 at 20:06:56.
 */
public class ReadKeyPasswordJsonAdapter extends TypeAdapter<ReadKeyPassword> {
    @Override
    public void write(JsonWriter out, ReadKeyPassword value) throws IOException {
        out.value(value.getValue());
    }
    @Override
    public ReadKeyPassword read(JsonReader in) throws IOException {
        return new ReadKeyPassword(in.nextString());
    }
}
