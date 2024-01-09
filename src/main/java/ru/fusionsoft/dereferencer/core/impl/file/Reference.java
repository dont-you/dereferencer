package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;

public class Reference {
    private final FragmentIdentifier fragmentIdentifier;
    private JsonNode fragment;

    public Reference(FragmentIdentifier fragmentIdentifier) {
        if(fragmentIdentifier==null)
            throw new NullPointerException("fragment identifier should not be null");

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
