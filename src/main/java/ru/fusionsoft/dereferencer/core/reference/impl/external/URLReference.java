package ru.fusionsoft.dereferencer.core.reference.impl.external;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.core.reference.impl.internal.RemoteReference;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class URLReference implements Reference {
    protected URI uri;
    protected JsonNode source = null;

    public URLReference(URI uri) {
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

        URLReference rightReference = (URLReference) obj;

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
        return ReferenceType.URL;
    }

    @Override
    public JsonNode getSource() throws ReferenceException {
        if (source == null) {
            try {
                URLConnection conn = uri.toURL().openConnection();
                if (conn.getContentType().contains("application/x-yaml")) {
                    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                    Object obj = yamlMapper.readValue(uri.toURL(), Object.class);

                    source = Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
                } else {
                    source = Dereferencer.objectMapper.readTree(uri.toURL());
                }

            } catch (IOException e) {
                throw new ReferenceException("error while getting json document from uri -{" + uri
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
