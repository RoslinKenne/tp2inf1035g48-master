package com.company.fields;

import java.lang.reflect.Field;

public abstract class FieldRef {
    protected final Field field;

    public FieldRef(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public String getName() {
        return field.getName();
    }

    public abstract String toJson(Object instance);

    public abstract void fromJson(String value, Object instance);
}
