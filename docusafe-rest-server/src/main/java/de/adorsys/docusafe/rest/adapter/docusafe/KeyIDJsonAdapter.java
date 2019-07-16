package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.docusafe.service.api.keystore.types.KeyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by peter on 13.01.18.
 */
public class KeyIDJsonAdapter extends TypeAdapter<KeyID> {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyIDJsonAdapter.class);

    @Override
    public void write(JsonWriter out, KeyID documentKeyID) throws IOException {
        out.value(documentKeyID.getValue());
    }
    @Override
    public KeyID read(JsonReader in) throws IOException {
        return new KeyID(in.nextString());
    }
}
