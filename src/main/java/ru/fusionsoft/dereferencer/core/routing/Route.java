package ru.fusionsoft.dereferencer.core.routing;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.routing.ref.Reference;
import ru.fusionsoft.dereferencer.core.routing.ref.ReferenceFactory;

public class Route implements Comparable<Route>{
    private Reference canonical = null;
    private Set<Reference> duplicates;

    public Route() {
        duplicates = new TreeSet<>();
    }

    public Route(Reference canonical) throws URIException {
        this.canonical = canonical;
        duplicates = new TreeSet<>();
        duplicates.add(canonical);
    }

    public Reference getCanonical() {
        return canonical;
    }

    public void setCanonical(Reference canonical) throws URIException {
        if (canonical == null)
            this.canonical = canonical;
        else
            this.canonical = ReferenceFactory.create(this.canonical, canonical);

        duplicates.add(canonical);
    }

    public Reference resolveRelative(String relative) throws URIException {
        return ReferenceFactory.create(canonical, URI.create(relative));
    }

    @Override
    public boolean equals(Object obj) {
        return duplicates.contains(((Route) obj).canonical);
    }

    @Override
    public int compareTo(Route route) {
        if(this.equals(route))
            return 0;
        else if(duplicates.size()==route.duplicates.size())
            return 1;
        else
            return duplicates.size() - route.duplicates.size();
    }

}
