package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.ResourceCenter;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

public class ResourceCenterBuilder {
    private URLLoader urlLoader;
    private URNResolver urnResolver;

    private ResourceCenterBuilder(){
        setURLLoader(new DefaultLoader())
                .setUrnResolver(null);
    }

    public static ResourceCenterBuilder builder(){
        return new ResourceCenterBuilder();
    };

    public ResourceCenter build(){
        return new BaseResourceCenter(urlLoader, urnResolver);
    };

    public ResourceCenterBuilder setURLLoader(URLLoader urlLoader) {
        this.urlLoader = urlLoader;
        return this;
    }

    public ResourceCenterBuilder setUrnResolver(URNResolver urnResolver) {
        this.urnResolver = urnResolver;
        return this;
    }
}
