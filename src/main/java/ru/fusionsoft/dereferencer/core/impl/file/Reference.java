package ru.fusionsoft.dereferencer.core.impl.file;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

public class Reference{
    private final FragmentIdentifier fragmentIdentifier;
    private JsonNode fragment;
    private final Set<BaseFile> subscribers;

    private Reference(FragmentIdentifier fragmentIdentifier){
        this.fragmentIdentifier = fragmentIdentifier;
        fragment = null;
        subscribers = new TreeSet<>();
    }

    public static ReferenceProxy getReferenceProxy(FragmentIdentifier fragmentIdentifier){
        return new ReferenceProxy(fragmentIdentifier);
    }

    public void subscribe(BaseFile subscriber){
        subscribers.add(subscriber);
        if(fragment!=null)
            subscriber.update(this, fragment);
    }

    public FragmentIdentifier getFragmentIdentifier() {
        return fragmentIdentifier;
    }

    static public class ReferenceProxy{
        private final Reference reference;

        private ReferenceProxy(FragmentIdentifier fragmentIdentifier){
            reference = new Reference(fragmentIdentifier);
        }

        public void setFragment(JsonNode fragment){
            reference.fragment = fragment;
            reference.subscribers.forEach(sub -> sub.update(reference, fragment));
        }

        public Reference getReference(){
            return reference;
        }

        public FragmentIdentifier getFragmentIdentifier(){
            return reference.getFragmentIdentifier();
        }
    }
}
