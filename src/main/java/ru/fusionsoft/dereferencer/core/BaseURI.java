package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class BaseURI {
    private URI canonical;
    private Set<URI> duplicates;

    public BaseURI(URI defaultBaseURI, URI canonical){
        duplicates = new HashSet<>();
        updateCanonical(defaultBaseURI.relativize(canonical));
    }

    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        return duplicates.contains(((BaseURI) obj).canonical);
    }

    public void updateCanonical(URI canonical){
        this.canonical = this.canonical.relativize(canonical);
        duplicates.add(this.canonical);
    }

    public void addDuplicates(Set<URI> duplicates){
        this.duplicates.addAll(duplicates);
    }

    public URI getCanonical(){
        return canonical;
    }
}
