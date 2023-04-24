package ru.fusionsoft.dereferencer.core.reference.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;


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
    public JsonNode getSource() throws ReferenceException {
        try {
            String fileName = file.getFileName().toString();
            if(fileName.substring(fileName.lastIndexOf(".")).equals("yaml")){
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                Object obj = yamlMapper.readValue(file.toFile(),Object.class);
                return Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
            }
            return Dereferencer.objectMapper.readTree(file.toFile());
        } catch (IOException e) {
            throw new ReferenceException("error while reading document from -{" + file
                    + "} with message - \n" + e.getMessage());
        }
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

    public Reference createUsingCurrent(String newPath) throws ReferenceException{
        try {
            return ReferenceFactory.create(new URI(directory+"/"+newPath));
        } catch (URISyntaxException e) {
            throw new ReferenceException("failed to create a new reference based on the current one with message: " + e.getMessage());
        }
    }
}
