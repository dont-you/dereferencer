package ru.fusionsoft.dereferencer.core;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class BaseURI {
    private URI canonical;
    private Set<URI> duplicates;

    public BaseURI(URI canonical){
        duplicates = new HashSet<>();
        updateCanonical(canonical);
    }

    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        return duplicates.contains(obj);
    }

    public void updateCanonical(URI canonical){
        this.canonical = canonical;
        duplicates.add(canonical);
    }

    public URI getCanonical(){
        return canonical;
    }
}
