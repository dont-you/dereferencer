package ru.fusionsoft.dereferencer.core.load;

import ru.fusionsoft.dereferencer.core.load.urn.TagURIResolver;
import ru.fusionsoft.dereferencer.core.load.urn.URNResolver;

public class BaseResourceCenterBuilder {
    private final BaseResourceCenter baseResourceCenter;

    private BaseResourceCenterBuilder() {
        baseResourceCenter = new BaseResourceCenter();
        setURNResolver(new TagURIResolver(baseResourceCenter)).setLoader(new URLLoader());
    }

    public static BaseResourceCenterBuilder getInstance() {
        return new BaseResourceCenterBuilder();
    }

    public BaseResourceCenter build() {
        return baseResourceCenter;
    }

    public BaseResourceCenterBuilder setURNResolver(URNResolver urnResolver) {
        baseResourceCenter.setURNResolver(urnResolver);
        return this;
    }

    public BaseResourceCenterBuilder setLoader(Loader loader) {
        baseResourceCenter.setLoader(loader);
        return this;
    }
}
