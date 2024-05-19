package ru.fusionsoft.dereferencer.core;
import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.Dereferencer;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public interface DereferencedFile {
    JsonNode getFragment(String path, Dereferencer dereferencer);
    JsonNode getFragmentImmediately(String path, Dereferencer dereferencer);
    URI getBaseURI();
}
