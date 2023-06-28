package ru.fusionsoft.dereferencer.core.utils.load.impl;

import java.io.InputStream;

import ru.fusionsoft.dereferencer.core.utils.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.utils.load.SupportedSourceTypes;

public class FileLoader implements SourceLoader{

    private static FileLoader instance = null;

    private FileLoader(){}

    @Override
    public InputStream getSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SupportedSourceTypes getSourceType() {
        // TODO Auto-generated method stub
        return null;
    }

    public static FileLoader getInstance(){
        if(instance==null)
            instance=new FileLoader();
        return instance;
    }
}
