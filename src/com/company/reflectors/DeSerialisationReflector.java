package com.company.reflectors;
import com.company.Json1035;
public class DeSerialisationReflector<T> {
    private final String json; private final Class<T> type;
    public DeSerialisationReflector(String json, Class<T> type) {this.json=json;this.type=type;}
    public T fromJson() {return new Json1035().deserialize(json,type);}
}
