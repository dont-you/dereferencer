package ru.fusionsoft.dereferencer.urn;

import java.net.URI;
import java.util.Map;

public interface URNCacheLoader {
    public Map<URN, URI> get(URI uri);
}
