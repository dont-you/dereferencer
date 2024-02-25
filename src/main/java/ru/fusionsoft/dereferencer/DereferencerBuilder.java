package ru.fusionsoft.dereferencer;

import java.net.URI;
import java.nio.file.Paths;

import ru.fusionsoft.dereferencer.allOf.AllOfFileFactory;
import ru.fusionsoft.dereferencer.core.*;
import ru.fusionsoft.dereferencer.core.file.BaseFileFactory;

public class DereferencerBuilder {
    private FileFactory fileFactory;
    private URI defaultBaseURI;

    private DereferencerBuilder() {
        setFileFactory(new BaseFileFactory())
                .setDefaultBaseURI(Paths.get(".").toAbsolutePath().normalize().toUri());
    }

    public static DereferencerBuilder builder() {
        return new DereferencerBuilder();
    }

    public Dereferencer build() {
        return new Dereferencer(fileFactory, defaultBaseURI);
    }

    public DereferencerBuilder enableAllOfMerge() {
        setFileFactory(new AllOfFileFactory());
        return this;
    }


    public DereferencerBuilder setFileFactory(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        return this;
    }

    public DereferencerBuilder setDefaultBaseURI(URI defaultBaseURI) {
        this.defaultBaseURI = defaultBaseURI;
        return this;
    }
}
