package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.net.URI;

public interface ResourceCenter {
    Resource load(URI uri) throws DereferenceException;
}
