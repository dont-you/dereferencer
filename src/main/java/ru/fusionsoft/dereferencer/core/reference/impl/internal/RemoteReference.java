package ru.fusionsoft.dereferencer.core.reference.impl.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class RemoteReference implements Reference {
    private URI uri;
    private JsonNode source = null;

    public RemoteReference(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RemoteReference))
            return false;

        if (hashCode() != obj.hashCode())
            return false;

        RemoteReference rightReference = (RemoteReference) obj;

        if (!uri.toString().equals(rightReference.uri.toString()))
            return false;

        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.REMOTE;
    }

    @Override
    public JsonNode getSource() throws ReferenceException {
        if (source == null) {
            String fileName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
            File file = Paths.get(uri.getPath()).toFile();
            try {
                if (fileName.substring(fileName.lastIndexOf(".")).equals("yaml")) {
                    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                    Object obj = yamlMapper.readValue(file, Object.class);
                    source = Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
                    return source;
                } else {
                    source = Dereferencer.objectMapper.readTree(file);
                }
            } catch (IOException e) {
                throw new ReferenceException("error while reading document from -{" + fileName
                        + "} with message - \n" + e.getMessage());
            }
        }
        return source;

    }

    @Override
    public Reference createNewReference(String uri) throws ReferenceException {
        return ReferenceFactory.createRelative(this, uri);
    }

    @Override
    public JsonNode setToSource(JsonNode setNode) throws ReferenceException {
        source = setNode;
        return source;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public String getFragment() {
        return "";
    }

}
