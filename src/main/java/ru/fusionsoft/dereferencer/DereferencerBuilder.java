package ru.fusionsoft.dereferencer;

import java.net.URI;
import java.nio.file.Paths;

import ru.fusionsoft.dereferencer.allOf.AllOfFileFactory;
import ru.fusionsoft.dereferencer.core.*;
import ru.fusionsoft.dereferencer.core.impl.file.BaseFileFactory;
import ru.fusionsoft.dereferencer.core.impl.load.BaseLoaderFactory;
import ru.fusionsoft.dereferencer.core.impl.urn.TagURIPool;

public class DereferencerBuilder {
    private URNPool urnPool;
    private LoaderFactory loaderFactory;
    private FileFactory fileFactory;
    private TypeAdapter typeAdapter;
    private URI defaultBaseURI;

    private DereferencerBuilder() {
        setUrnPool(new TagURIPool()).setLoaderFactory(new BaseLoaderFactory()).setFileFactory(new BaseFileFactory()).setTypeAdapter(new BaseTypeAdapter())
                .setDefaultBaseURI(Paths.get(".").toAbsolutePath().normalize().toUri());
    }

    public static DereferencerBuilder builder() {
        return new DereferencerBuilder();
    }

    public Dereferencer build() {
        return new Dereferencer(urnPool, loaderFactory, fileFactory, typeAdapter, defaultBaseURI);
    }

    public DereferencerBuilder enableAllOfMerge() {
        setFileFactory(new AllOfFileFactory());
        return this;
    }

    public DereferencerBuilder setUrnPool(URNPool urnPool) {
        this.urnPool = urnPool;
        return this;
    }

    public DereferencerBuilder setTypeAdapter(TypeAdapter typeAdapter) {
        this.typeAdapter = typeAdapter;
        return this;
    }
    public DereferencerBuilder setLoaderFactory(LoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
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
