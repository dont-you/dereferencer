package ru.fusionsoft.dereferencer.core.reference.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;


public class URLReference implements Reference{
    private URI uri;

    public URLReference(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // TODO
        return super.hashCode();
    }

    @Override
    public String toString() {
        // TODO
        return super.toString();
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.URL;
    }

    @Override
    public JsonNode getSource() throws ReferenceException{
        try {
            URLConnection conn = uri.toURL().openConnection();
            if(conn.getContentType().contains("application/x-yaml")){
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                Object obj = yamlMapper.readValue(uri.toURL(),Object.class);

                return Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
            }

            return Dereferencer.objectMapper.readTree(uri.toURL());
        } catch (IOException e) {
            throw new ReferenceException("error while getting json document from uri -{" + uri
                    + "} with message - \n" + e.getMessage());
        }
    }

    @Override
    public Reference createNewReference(String uri) throws ReferenceException{
        try {
            URI newUri = new URI(uri);
            return ReferenceFactory.create(new URI(this.uri.getScheme(),
                    this.uri.getAuthority(),
                    this.uri.getPath().substring(this.uri.getPath().lastIndexOf("/")) + "/" +
                    newUri.getPath(),
                    newUri.getFragment()
            ));
        } catch (URISyntaxException e) {
            throw new ReferenceException("failed to create a new reference with message: " + e.getMessage());
        }
    }

    @Override
    public JsonNode setToSource(JsonNode setNode) throws ReferenceException {
        // TODO Auto-generated method stub
        return getSource();
    }

}
