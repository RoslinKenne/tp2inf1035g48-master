package com.company.reflectors;

import com.company.annotations.Ignored;
import com.company.fields.CollectionFieldRef;
import com.company.fields.FieldRef;
import com.company.fields.ObjectFieldRef;
import com.company.fields.SimpleFieldRef;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SerialisationReflector {

    private final List<FieldRef> simpleFields = new ArrayList<>();
    private final List<FieldRef> objectFields = new ArrayList<>();
    private final List<FieldRef> collectionFields = new ArrayList<>();

    public SerialisationReflector(Object instance) {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Ignored.class) != null) {
                continue; // Ignorer les champs annotés avec @Ignored
            }

            if (isSimpleField(field)) {
                simpleFields.add(new SimpleFieldRef(field));
            } else if (isCollectionField(field)) {
                collectionFields.add(new CollectionFieldRef(field));
            } else {
                objectFields.add(new ObjectFieldRef(field));
            }
        }
    }

    public String toJson(Object instance) {
        StringBuilder json = new StringBuilder("{");

        for (FieldRef field : simpleFields) {
            String fieldJson = field.toJson(instance);
            if (!fieldJson.isEmpty()) {
                json.append(fieldJson).append(", ");
            }
        }

        for (FieldRef field : objectFields) {
            String fieldJson = field.toJson(instance);
            if (!fieldJson.isEmpty()) {
                json.append(fieldJson).append(", ");
            }
        }

        for (FieldRef field : collectionFields) {
            String fieldJson = field.toJson(instance);
            if (!fieldJson.isEmpty()) {
                json.append(fieldJson).append(", ");
            }
        }

        if (json.length() > 1 && json.charAt(json.length() - 2) == ',') {
            json.setLength(json.length() - 2); // Supprimer la dernière virgule
        }

        json.append("}");
        return json.toString();
    }

    private boolean isSimpleField(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class;
    }

    private boolean isCollectionField(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }
}
