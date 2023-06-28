package ru.fusionsoft.dereferencer.core.routing.ref;

public class JsonPtr {
    private String jsonPointer = null;
    private String plainName = null;
    private JsonPtr parentJsonPointer = null;
    private String propertyName = null;

    public JsonPtr(String fragment) {
        if (fragment.startsWith("/") || fragment.equals(""))
            jsonPointer = fragment;
        else
            plainName = fragment;
    }

    public boolean isResolved() {
        return jsonPointer != null;
    }

    public String getResolved() {
        return jsonPointer;
    }

    public String getPointer() {
        return jsonPointer;
    }

    public void setPointer(String pointer) {
        this.jsonPointer = pointer;
    }

    public String getPlainName() {
        return plainName;
    }

    public void setPlainName(String plainName) {
        this.plainName = plainName;
    }

    public JsonPtr getParent() {
        if (parentJsonPointer == null)
            return parentJsonPointer = new JsonPtr(jsonPointer.substring(0, jsonPointer.lastIndexOf("/")));
        else
            return parentJsonPointer;
    }

    public String getPropertyName() {
        if (parentJsonPointer == null)
            return propertyName = propertyName.substring(propertyName.lastIndexOf("/") + 1, propertyName.length());
        else
            return propertyName;
    }

    public JsonPtr subtractPtr(JsonPtr ptr) {
        return new JsonPtr(ptr.getResolved().replace(jsonPointer, ""));
    }

    public boolean isSuperSetTo(JsonPtr ptr) {
        if (ptr.isResolved() && this.isResolved() && ptr.getResolved().length() != this.getResolved().length())
            return ptr.getResolved().startsWith(this.getResolved());
        else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO
        return false;
    }
}
