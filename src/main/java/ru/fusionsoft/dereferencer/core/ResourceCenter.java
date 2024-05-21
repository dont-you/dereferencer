package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.load.Resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface ResourceCenter {
    Resource load(URI uri) throws Exception;
}
