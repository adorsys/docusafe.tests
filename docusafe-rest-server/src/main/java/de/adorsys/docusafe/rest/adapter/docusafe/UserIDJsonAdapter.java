package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.docusafe.service.api.types.UserID;

import java.io.IOException;

/**
 * Created by peter on 22.01.2018 at 20:05:52.
 */
public class UserIDJsonAdapter extends TypeAdapter<UserID> {
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
