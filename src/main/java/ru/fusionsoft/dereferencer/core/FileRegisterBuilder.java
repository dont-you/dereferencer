package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.load.ResourceCenterBuilder;

import java.util.logging.Logger;

public class FileRegisterBuilder {
    private DereferencedFileFactory dereferencedFileFactory;
    private ResourceCenter resourceCenter;
    private Logger logger;

    private FileRegisterBuilder(){
        setDereferencedFileFactory(new DereferencedFileFactory())
                .setResourceCenter(ResourceCenterBuilder.builder().build())
                .setLogger(Logger.getLogger(FileRegister.class.getName()));
    }

    public static FileRegisterBuilder builder(){
        return new FileRegisterBuilder();
    };

    public FileRegister build(){
        return new FileRegister(dereferencedFileFactory, resourceCenter, logger);
    };

    public FileRegisterBuilder setDereferencedFileFactory(DereferencedFileFactory dereferencedFileFactory) {
        this.dereferencedFileFactory = dereferencedFileFactory;
        return this;
    }

    public FileRegisterBuilder setResourceCenter(ResourceCenter resourceCenter) {
        this.resourceCenter = resourceCenter;
        return this;
    }

    public FileRegisterBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
