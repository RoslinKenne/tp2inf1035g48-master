package com.company.reflectors;
import com.company.Json1035;
public class SerialisationReflector {
    public SerialisationReflector(Object instance) {}
    public String toJson(Object instance) { return new Json1035().serialize(instance); }
}
