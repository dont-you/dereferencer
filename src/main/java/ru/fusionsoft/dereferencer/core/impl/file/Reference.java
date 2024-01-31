package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

public class Reference {
    private final FragmentIdentifier fragmentIdentifier;
    private final Set<ReferenceListener> listeners;
    private JsonNode fragment;

    public Reference(FragmentIdentifier fragmentIdentifier) {
        if (fragmentIdentifier == null)
            throw new NullPointerException("fragment identifier should not be null");

        this.fragmentIdentifier = fragmentIdentifier;
        listeners = new HashSet<>();
        fragment = null;
    }

    public void setFragment(JsonNode fragment) {
        this.fragment = fragment;
        listeners.forEach(l -> l.update(this));
    }

    public JsonNode getFragment() {
        return fragment;
    }

    public boolean isResolved() {
        return fragment != null;
    }

    public void subscribe(ReferenceListener listener) {
        listeners.add(listener);
        if (fragment != null)
            listener.update(this);
    }

    public FragmentIdentifier getFragmentIdentifier() {
        return fragmentIdentifier;
    }
}
