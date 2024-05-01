package ru.fusionsoft.dereferencer.core;
import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.Dereferencer;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public interface DereferencedFile {
    JsonNode getFragment(String path, Dereferencer dereferencer) throws ExecutionException, InterruptedException;
    JsonNode getFragmentImmediately(String path, Dereferencer dereferencer) throws ExecutionException, InterruptedException;
    URI getBaseURI();
}
