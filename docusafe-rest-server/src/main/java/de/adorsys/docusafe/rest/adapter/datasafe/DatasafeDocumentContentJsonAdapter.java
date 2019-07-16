package de.adorsys.docusafe.rest.adapter.datasafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.adorsys.common.utils.HexUtil;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;

import java.io.IOException;

/**
 * Created by peter on 27.02.2018 at 11:13:30.
 */
public class DatasafeDocumentContentJsonAdapter extends TypeAdapter<DocumentContent> {
    @Override
    public void write(JsonWriter out, DocumentContent value) throws IOException {
        out.value(HexUtil.convertBytesToHexString(value.getValue()));
    }
    @Override
    public DocumentContent read(JsonReader in) throws IOException {
        return new DocumentContent(HexUtil.convertHexStringToBytes(in.nextString()));
    }
}
