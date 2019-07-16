package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URL;

public class URLJsonAdapter extends TypeAdapter<URL> {
    @Override
    public void write(JsonWriter out, URL url) throws IOException {
        out.value(url.toString());
    }
    @Override
    public URL read(JsonReader in) throws IOException {
        return new URL(in.nextString());
    }
}

