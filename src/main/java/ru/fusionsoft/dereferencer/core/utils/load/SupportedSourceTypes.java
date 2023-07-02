package ru.fusionsoft.dereferencer.core.utils.load;

public enum SupportedSourceTypes {
    JSON,
    YAML,
    NOT_IMPLEMENTED;

    public boolean isYaml() {
        return this.equals(YAML);
    }

    public boolean isJson() {
        return this.equals(JSON);
    }

    public boolean isNotImplemented() {
        return this.equals(NOT_IMPLEMENTED);
    }

    public static SupportedSourceTypes resolveSourceType(String extension) {
        switch (extension) {
            case "json":
                return JSON;
            case "yaml":
                return YAML;
            case "x-yaml":
                return YAML;
            default:
                return NOT_IMPLEMENTED;
        }
    }
}
