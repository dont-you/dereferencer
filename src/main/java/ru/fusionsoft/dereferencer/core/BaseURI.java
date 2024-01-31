package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class BaseURI implements Comparable<BaseURI> {
    private URI canonical;
    private final Set<URI> duplicates;

    public BaseURI(URI defaultBaseURI, URI canonical) {
        duplicates = new HashSet<>();
        this.canonical = defaultBaseURI;
        updateCanonical(canonical);
    }

    public void updateCanonical(URI canonical) {
        this.canonical = this.canonical.resolve(canonical);
        duplicates.add(this.canonical);
    }

    public void updateCanonical(URL canonical) {
        try {
            this.canonical = this.canonical.resolve(canonical.toURI());
        } catch (URISyntaxException e) {
            throw new DereferenceRuntimeException("for some reason, url - " + canonical + " could not be parsed to uri");
        }
        duplicates.add(this.canonical);
    }

    public URI getCanonical() {
        return canonical;
    }

    @Override
    public int compareTo(BaseURI arg0) {
        if (duplicates.contains(arg0.canonical))
            return 0;
        else if (duplicates.size() >= arg0.duplicates.size())
            return 1;
        else
            return -1;
    }
}
