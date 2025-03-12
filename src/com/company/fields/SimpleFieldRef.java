package com.company.fields;
import java.lang.reflect.Field;

public class SimpleFieldRef extends FieldRef {

    public SimpleFieldRef(Field field) {
        super(field);
    }

    @Override
    public String toJson(Object instance) {
        try {
            Object value = field.get(instance);
            return "\"" + getName() + "\": \"" + value + "\"";
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la sérialisation du champ simple : " + field.getName(), e);
        }
    }

    @Override
    public void fromJson(String value, Object instance) {
        try {
            if (field.getType() == int.class || field.getType() == Integer.class) {
                field.set(instance, Integer.parseInt(value));
            } else if (field.getType() == String.class) {
                field.set(instance, value);
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.set(instance, Boolean.parseBoolean(value));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la désérialisation du champ simple : " + field.getName(), e);
        }
    }
}
