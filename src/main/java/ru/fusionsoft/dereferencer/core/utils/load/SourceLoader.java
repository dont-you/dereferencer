package ru.fusionsoft.dereferencer.core.utils.load;

import java.io.InputStream;

public interface SourceLoader {
    public InputStream getSource();
    public SupportedSourceTypes getSourceType();
}
