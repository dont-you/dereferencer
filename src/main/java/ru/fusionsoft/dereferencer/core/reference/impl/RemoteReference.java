package ru.fusionsoft.dereferencer.core.reference.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
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


public class RemoteReference implements Reference{
    URI uri;
    private Path directory;
    private Path file;
    private String fragment;
    private JsonNode source = null;

    public RemoteReference(URI uri) {
        this.uri = uri;
        this.directory = Paths.get(uri.getPath()).toAbsolutePath().getParent();
        this.file = Paths.get(uri.getPath()).toAbsolutePath();
        this.fragment = uri.getFragment();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof RemoteReference))
            return false;

        if(hashCode() != obj.hashCode())
            return false;

        RemoteReference rightReference = (RemoteReference) obj;

        if(!file.toAbsolutePath().toString().equals(rightReference.file.toAbsolutePath().toString()))
            return false;
        if(!fragment.equals(rightReference.fragment))
            return false;

        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hash(file.toAbsolutePath(), fragment);
    }

    @Override
    public String toString() {
        return file.toAbsolutePath().toString() + "#" + fragment;
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.REMOTE;
    }

    @Override
    public JsonNode getSource() throws ReferenceException {
        try {
            if(source == null){

                String fileName = file.getFileName().toString();
                if(fileName.substring(fileName.lastIndexOf(".")).equals("yaml")){
                    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                    Object obj = yamlMapper.readValue(file.toFile(),Object.class);
                    source =  Dereferencer.objectMapper.readTree(Dereferencer.objectMapper.writeValueAsString(obj));
                    return source;
                }
                source = Dereferencer.objectMapper.readTree(file.toFile());
            }
            if (fragment!=""){
                return source.at(fragment);
            }
            return source;

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

    @Override
    public Reference createNewReference(String uri) throws ReferenceException {
        try {
            if(ReferenceType.isLocalReference(new URI(uri))){
                return new LocalReference(new URI(this.uri.getScheme(), this.uri.getAuthority(), this.uri.getPath(), this.uri.getQuery(), uri.substring(1)),
                                          source);
            }

            if(Paths.get(uri).isAbsolute()){
                return ReferenceFactory.create(new URI(uri));
            } else if(ReferenceType.isURLReference(new URI(uri))){
                return ReferenceFactory.create(new URI(uri));
            }
            return ReferenceFactory.create(new URI(directory+"/"+uri));
        } catch (URISyntaxException e) {
            throw new ReferenceException("failed to create a new reference with message: " + e.getMessage());
        }
    }

    @Override
    public JsonNode setToSource(JsonNode setNode) throws ReferenceException {
        source = setNode;
        return source;
    }

}
