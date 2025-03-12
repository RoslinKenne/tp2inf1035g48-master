package com.company.reflectors;

import com.company.fields.CollectionFieldRef;
import com.company.fields.FieldRef;
import com.company.fields.ObjectFieldRef;
import com.company.fields.SimpleFieldRef;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DeSerialisationReflector<T> {
    private final List<FieldRef> simpleFields = new ArrayList<>();
    private final List<FieldRef> objectFields = new ArrayList<>();
    private final List<FieldRef> collectionFields = new ArrayList<>();
    private final String json;
    private final Class<T> toType;

    public DeSerialisationReflector(String json, Class<T> toType) {
        this.json = json.trim();
        this.toType = toType;

        // Initialisation des champs
        initFields();
    }

    private void initFields() {
        Class<?> clazz = toType;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isPrimitive() || field.getType() == String.class) {
                simpleFields.add(new SimpleFieldRef(field));
            } else if (List.class.isAssignableFrom(field.getType())) {
                collectionFields.add(new CollectionFieldRef(field));
            } else {
                objectFields.add(new ObjectFieldRef(field));
            }
        }
    }

    public T fromJson() {
        try {
            T instance = toType.getDeclaredConstructor().newInstance();
            String cleanJson = json.substring(1, json.length() - 1); // Retirer accolades
            StringTokenizer tokenizer = new StringTokenizer(cleanJson, ",:{}\"", true);

            String currentKey = null;

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();

                if (token.equals(",") || token.equals("{") || token.equals("}") || token.isEmpty()) {
                    continue;
                }

                if (currentKey == null) {
                    currentKey = token; // Assigner une clé
                } else if (!token.equals(":")) {
                    FieldRef ref = findField(currentKey);
                    if (ref != null) {
                        ref.fromJson(token, instance);
                    }
                    currentKey = null; // Réinitialiser la clé
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation", e);
        }
    }

    private FieldRef findField(String key) {
        // Rechercher dans les champs simples
        for (FieldRef field : simpleFields) {
            if (field.getName().equals(key)) {
                return field;
            }
        }
        // Rechercher dans les champs objets
        for (FieldRef field : objectFields) {
            if (field.getName().equals(key)) {
                return field;
            }
        }
        // Rechercher dans les champs collections
        for (FieldRef field : collectionFields) {
            if (field.getName().equals(key)) {
                return field;
            }
        }
        return null;
    }
}
