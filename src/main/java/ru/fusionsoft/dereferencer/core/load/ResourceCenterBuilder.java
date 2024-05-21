package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

import java.util.logging.Logger;

public class ResourceCenterBuilder {
    private URLLoader urlLoader;
    private URNResolver urnResolver;
    private Logger logger;

    private ResourceCenterBuilder(){
        setURLLoader(new DefaultLoader())
                .setUrnResolver(null)
                .setLogger(Logger.getLogger(BaseResourceCenter.class.getName()));
    }

    public static ResourceCenterBuilder builder(){
        return new ResourceCenterBuilder();
    };

    public ResourceCenter build(){
        return new BaseResourceCenter(urlLoader, urnResolver, logger);
    };

    public ResourceCenterBuilder setURLLoader(URLLoader urlLoader) {
        this.urlLoader = urlLoader;
        return this;
    }

    public ResourceCenterBuilder setUrnResolver(URNResolver urnResolver) {
        this.urnResolver = urnResolver;
        return this;
    }

    public ResourceCenterBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
