package com.company;

import com.company.reflectors.SerialisationReflector;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Json1035 {

    // Sérialisation d'un objet en JSON
    public String serialize(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            // Utilisation de SerialisationReflector
            SerialisationReflector reflector = new SerialisationReflector(o);
            return reflector.toJson(o); // Correction pour appeler toJson correctement
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sérialisation : ", e);
        }
    }

    // Désérialisation d'une chaîne JSON en objet
    public <T> T deserialize(String json, Class<T> toType) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("Le JSON fourni est vide ou null.");
        }

        StringTokenizer tokenizer = new StringTokenizer(json, " ;,:{}[]\"");
        try {
            // Instanciation de l'objet cible
            T instance = toType.getDeclaredConstructor().newInstance();

            String currentKey = null; // Stocker la clé courante

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();

                if (currentKey == null) {
                    currentKey = token; // Récupération de la clé
                } else {
                    Field field = toType.getDeclaredField(currentKey);
                    field.setAccessible(true);

                    // Vérification du type du champ
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(instance, Integer.parseInt(token));
                    } else if (field.getType() == String.class) {
                        field.set(instance, token);
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(instance, Boolean.parseBoolean(token));
                    } else if (field.getType() == List.class) {
                        // Gestion des listes
                        List<String> list = new ArrayList<>();
                        while (tokenizer.hasMoreTokens()) {
                            String item = tokenizer.nextToken().trim();
                            if (item.equals("]")) break;
                            list.add(item);
                        }
                        field.set(instance, list);
                    } else {
                        // Désérialisation récursive pour les objets imbriqués
                        String subJson = extractSubJson(json, currentKey);
                        Object subObject = deserialize(subJson, field.getType());
                        field.set(instance, subObject);
                    }

                    currentKey = null; // Réinitialiser pour la prochaine clé
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation : ", e);
        }
    }

    // Extraction du sous-JSON pour les objets imbriqués
    private String extractSubJson(String json, String key) {
        int startIndex = json.indexOf(key) + key.length() + 2; // Trouver le début
        int braceCount = 0;
        StringBuilder subJson = new StringBuilder();

        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            subJson.append(c);
            if (braceCount == 0) break;
        }

        return subJson.toString();
    }
}
