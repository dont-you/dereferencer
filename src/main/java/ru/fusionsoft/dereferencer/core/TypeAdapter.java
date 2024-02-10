package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;

public interface TypeAdapter {
    JsonNode readJsonFrom(InputStream inputStream, String mimetype) throws IOException;
}
