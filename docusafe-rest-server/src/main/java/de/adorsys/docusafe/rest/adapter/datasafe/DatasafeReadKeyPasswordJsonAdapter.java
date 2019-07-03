package de.adorsys.docusafe.rest.adapter.datasafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword;

import java.io.IOException;

public class DatasafeReadKeyPasswordJsonAdapter extends TypeAdapter<ReadKeyPassword> {
    @Override
    public void write(JsonWriter out, ReadKeyPassword value) throws IOException {
        out.value(value.getValue());
    }
    @Override
    public ReadKeyPassword read(JsonReader in) throws IOException {
        return new ReadKeyPassword(in.nextString());
    }
}
