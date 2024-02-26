package ru.fusionsoft.dereferencer.core;

import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class Resource {
    private final Set<URI> duplicates;
    private URI retrieval;
    private InputStream stream;
    private String mimetype;

    public Resource(URI retrieval) {
        this.retrieval = retrieval;
        this.duplicates = new HashSet<>();
        stream = null;
        mimetype = null;
    }

    public void updateRetrieval(URI retrieval) {
        duplicates.add(this.retrieval);
        this.retrieval = retrieval;
    }

    public URI[] getDuplicates() {
        return duplicates.toArray(URI[]::new);
    }

    public void addDuplicate(URI uri) {
        duplicates.add(uri);
    }

    public URI getRetrieval() {
        return retrieval;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
}
