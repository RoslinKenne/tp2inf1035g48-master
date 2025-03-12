package com.company.fields;

import com.company.reflectors.DeSerialisationReflector;
import com.company.reflectors.SerialisationReflector;

import java.lang.reflect.Field;

public class ObjectFieldRef extends FieldRef {

    public ObjectFieldRef(Field field) {
        super(field);
    }

    @Override
    public String toJson(Object instance) {
        try {
            Object value = field.get(instance);
            if (value == null) {
                return "\"" + getName() + "\": null";
            }
            // Correction : Passer "value" au constructeur de SerialisationReflector
            return "\"" + getName() + "\": " + new SerialisationReflector(value).toJson(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la sérialisation du champ objet : " + field.getName(), e);
        }
    }

    @Override
    public void fromJson(String value, Object instance) {
        try {
            Object subInstance = new DeSerialisationReflector<>(value, field.getType()).fromJson();
            field.set(instance, subInstance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la désérialisation du champ objet : " + field.getName(), e);
        }
    }
}
