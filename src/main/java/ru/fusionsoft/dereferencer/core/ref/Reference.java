package ru.fusionsoft.dereferencer.core.ref;

import java.net.URI;

public abstract class Reference{
    private URI defaultBaseUri = null;
    private URI retrievalUri = null;
    private URI embeddedInContentUri = null;
    private JsonPtr jsonPointer;
    private int hashCode;

    // TODO
}
