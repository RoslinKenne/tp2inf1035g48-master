package com.company.fields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CollectionFieldRef extends FieldRef {

    public CollectionFieldRef(Field field) {
        super(field);
    }

    @Override
    public String toJson(Object instance) {
        try {
            List<?> list = (List<?>) field.get(instance);
            StringBuilder json = new StringBuilder("[");
            for (Object item : list) {
                json.append("\"").append(item).append("\", ");
            }
            if (!list.isEmpty()) json.setLength(json.length() - 2); // Supprimer la dernière virgule
            json.append("]");
            return "\"" + getName() + "\": " + json;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la sérialisation du champ collection : " + field.getName(), e);
        }
    }

    @Override
    public void fromJson(String value, Object instance) {
        try {
            List<String> list = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(value, "[]\", ");
            while (tokenizer.hasMoreTokens()) {
                list.add(tokenizer.nextToken());
            }
            field.set(instance, list);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur lors de la désérialisation du champ collection : " + field.getName(), e);
        }
    }
}
