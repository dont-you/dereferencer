package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.load.ResourceCenterBuilder;

public class FileRegisterBuilder {
    private DereferencedFileFactory dereferencedFileFactory;
    private ResourceCenter resourceCenter;

    private FileRegisterBuilder(){
        setDereferencedFileFactory(new DereferencedFileFactory())
                .setResourceCenter(ResourceCenterBuilder.builder().build());
    }

    public static FileRegisterBuilder builder(){
        return new FileRegisterBuilder();
    };

    public FileRegister build(){
        return new FileRegister(dereferencedFileFactory, resourceCenter);
    };

    public FileRegisterBuilder setDereferencedFileFactory(DereferencedFileFactory dereferencedFileFactory) {
        this.dereferencedFileFactory = dereferencedFileFactory;
        return this;
    }

    public FileRegisterBuilder setResourceCenter(ResourceCenter resourceCenter) {
        this.resourceCenter = resourceCenter;
        return this;
    }
}
