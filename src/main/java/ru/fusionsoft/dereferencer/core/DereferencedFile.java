package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.Dereferencer;

import java.net.URI;

public interface DereferencedFile {
    JsonNode getFragment(String path, Dereferencer dereferencer);

    JsonNode getFragmentImmediately(String path, Dereferencer dereferencer);

    URI getBaseURI();
}
