package ru.fusionsoft.dereferencer.utils;

import java.net.URI;
import java.util.List;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;

public interface SourceClient {
    public List<String> directoryList(URI uri) throws LoadException;
}
