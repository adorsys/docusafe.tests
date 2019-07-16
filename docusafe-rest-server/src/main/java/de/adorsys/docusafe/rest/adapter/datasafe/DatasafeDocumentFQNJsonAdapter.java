package de.adorsys.docusafe.rest.adapter.datasafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;

import java.io.IOException;

/**
 * Created by peter on 24.01.2018 at 11:19:56.
 */
public class DatasafeDocumentFQNJsonAdapter extends TypeAdapter<DocumentFQN> {
    @Override
    public void write(JsonWriter out, DocumentFQN value) throws IOException {
        out.value(value.getDocusafePath());
    }
    @Override
    public DocumentFQN read(JsonReader in) throws IOException {
        return new DocumentFQN(in.nextString());
    }
}
