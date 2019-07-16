package de.adorsys.docusafe.rest.adapter.datasafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.datasafe.encrypiton.api.types.UserID;

import java.io.IOException;

public class DatasafeUserIDJsonAdapter extends TypeAdapter<UserID> {
    @Override
    public void write(JsonWriter out, UserID value) throws IOException {
        if (value == null) {
            String nullString = null;
            out.value(nullString);
            return;
        }
        out.value(value.getValue());
    }

    @Override
    public UserID read(JsonReader in) throws IOException {
        return new UserID(in.nextString());
    }
}

