package ru.fusionsoft.dereferencer.core.reference.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;


public class LocalReference implements Reference{
    private URI uri;
    private Path directory;
    private Path file;
    private String fragment;
    private JsonNode source = null;

    public LocalReference(URI uri, JsonNode source) {
        this.uri = uri;
        this.directory = Paths.get(uri.getPath()).toAbsolutePath().getParent();
        this.file = Paths.get(uri.getPath()).toAbsolutePath();
        this.fragment = uri.getFragment();
        this.source = source;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof RemoteReference))
            return false;

        if(hashCode() != obj.hashCode())
            return false;

        LocalReference rightReference = (LocalReference) obj;

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
        return source.at(fragment);
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
        String parentRef = fragment.substring(0,fragment.lastIndexOf("/"));
        String propName = fragment.substring(fragment.lastIndexOf("/")+1);
        ObjectNode node = (ObjectNode) source.at(parentRef);
        node.set(propName, setNode);
        return source.at(fragment);
    }

}
