package com.company.fields;
import java.lang.reflect.Field;
import com.company.Json1035;
public class ObjectFieldRef extends FieldRef {
    public ObjectFieldRef(Field field) {super(field);}
    public String toJson(Object instance) {
        try {return Json1035.quote(getName())+":"+new Json1035().serialize(field.get(instance));}
        catch(IllegalAccessException e){throw new IllegalArgumentException(e);}
    }
    public void fromJson(String value,Object instance) {
        try {field.set(instance,new Json1035().deserializeType(value,field.getGenericType()));}
        catch(IllegalAccessException e){throw new IllegalArgumentException(e);}
    }
}
