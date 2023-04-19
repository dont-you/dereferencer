package ru.fusionsoft.dereferencer.reference.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.reference.Reference;
import ru.fusionsoft.dereferencer.reference.ReferenceType;

public class RemoteReference implements Reference{
    private Path directory;
    private Path file;
    private String fragment;

    public RemoteReference(URI uri) {
        this.directory = Paths.get(uri.getPath()).toAbsolutePath().getParent();
        this.file = Paths.get(uri.getPath()).toAbsolutePath();
        this.fragment = uri.getFragment();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.REMOTE;
    }

    @Override
    public JsonNode getSource() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(file.toFile());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Path getDirectory() {
        return directory;
    }

    public Path getFile() {
        return file;
    }

    public String getFragment() {
        return fragment;
    }
}
