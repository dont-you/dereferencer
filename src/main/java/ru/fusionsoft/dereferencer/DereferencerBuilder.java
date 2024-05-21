package ru.fusionsoft.dereferencer;

import ru.fusionsoft.dereferencer.core.DereferencedFileFactory;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.FileRegisterBuilder;
import ru.fusionsoft.dereferencer.core.load.BaseResourceCenter;
import ru.fusionsoft.dereferencer.core.load.DefaultLoader;
import ru.fusionsoft.dereferencer.core.load.urn.TagURIResolver;

import java.net.URI;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DereferencerBuilder {
    private ExecutorService executorService;
    private FileRegister fileRegister;
    private URI defaultBaseURI;

    private DereferencerBuilder(){
        setDefaultBaseURI(Paths.get(".").toAbsolutePath().normalize().toUri())
                .setFileRegister(FileRegisterBuilder.builder().build())
                .setExecutorService(Executors.newVirtualThreadPerTaskExecutor());
    }

    public static DereferencerBuilder builder(){
        return new DereferencerBuilder();
    };

    public Dereferencer build(){
        return new Dereferencer(executorService, fileRegister, defaultBaseURI);
    };

    public DereferencerBuilder setDefaultBaseURI(URI defaultBaseURI) {
        this.defaultBaseURI = defaultBaseURI;
        return this;
    }

    public DereferencerBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public DereferencerBuilder setFileRegister(FileRegister fileRegister) {
        this.fileRegister = fileRegister;
        return this;
    }
}
