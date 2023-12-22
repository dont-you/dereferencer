package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;

public class Reference {
    private final FragmentIdentifier fragmentIdentifier;
    private JsonNode fragment;

    public Reference(FragmentIdentifier fragmentIdentifier) {
        this.fragmentIdentifier = fragmentIdentifier;
        fragment = null;
    }

    public void setFragment(JsonNode fragment) {
        this.fragment = fragment;
    }

    public JsonNode getFragment() {
        return fragment;
    }

    public FragmentIdentifier getFragmentIdentifier() {
        return fragmentIdentifier;
    }
}
