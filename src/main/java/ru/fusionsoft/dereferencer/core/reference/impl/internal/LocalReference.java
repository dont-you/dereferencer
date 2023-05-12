package ru.fusionsoft.dereferencer.core.reference.impl.internal;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.core.reference.factories.ReferenceFactory;
import ru.fusionsoft.dereferencer.enums.ReferenceType;
import ru.fusionsoft.dereferencer.exception.ReferenceException;

public class LocalReference implements Reference {

    Reference parentReference;
    String fragment;
    private JsonNode source = null;

    public LocalReference(Reference parentReference, String fragment) {
        this.parentReference = parentReference;
        this.fragment = fragment;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof LocalReference))
            return false;

        if (hashCode() != obj.hashCode())
            return false;

        LocalReference rightReference = (LocalReference) obj;

        if (!parentReference.equals(rightReference.parentReference))
            return false;
        if (!fragment.equals(rightReference.fragment))
            return false;

        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hash(parentReference, fragment);
    }

    @Override
    public String toString() {
        return parentReference.toString() + "#" + fragment;
    }

    @Override
    public ReferenceType getReferenceType() {
        return ReferenceType.LOCAL;
    }

    @Override
    public JsonNode getSource() throws ReferenceException {
        if (source == null) {
            Dereferencer.getLogger().info("trying get source from reference with uri - '" + this.getUri() + "'");
            source = parentReference.getSource();
        }
        return source.at(fragment);
    }

    @Override
    public String getFragment() {
        return fragment;
    }

    @Override
    public Reference createNewReference(String uri) throws ReferenceException {
        return ReferenceFactory.createRelative(parentReference, uri);
    }

    @Override
    public JsonNode setToSource(JsonNode setNode) throws ReferenceException {
        String parentRef = fragment.substring(0, fragment.lastIndexOf("/"));
        String propName = fragment.substring(fragment.lastIndexOf("/") + 1);
        ObjectNode node = (ObjectNode) source.at(parentRef);
        node.set(propName, setNode);
        return source.at(fragment);
    }

    @Override
    public URI getUri() {
        return URI.create(this.toString());
    }

}
