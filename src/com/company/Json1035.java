package com.company;

import com.company.annotations.Alias;
import com.company.annotations.Ignored;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

/** JSON sans dépendance : objets, listes typées, tableaux, scalaires et annotations. */
public class Json1035 {
    public String serialize(Object value) {
        return write(value, Collections.newSetFromMap(new IdentityHashMap<>()));
    }
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String json, Class<T> type) {
        return (T) deserializeType(json, type);
    }
    public Object deserializeType(String json, Type type) {
        if (json == null) throw new IllegalArgumentException("JSON null");
        Parser parser = new Parser(json);
        Object value = parser.value(0); parser.space();
        if (parser.i != json.length()) throw parser.error("Contenu après la valeur JSON");
        try { return convert(value, type); }
        catch(ArithmeticException e) { throw new IllegalArgumentException("Nombre incompatible avec le type demandé",e); }
    }
    private static List<Field> fields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c=type; c!=null && c!=Object.class; c=c.getSuperclass())
            for (Field f:c.getDeclaredFields())
                if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())
                    && !f.isSynthetic() && !f.isAnnotationPresent(Ignored.class)) {
                    f.setAccessible(true); fields.add(f);
                }
        return fields;
    }
    private static String name(Field f) {
        Alias a=f.getAnnotation(Alias.class);
        return a!=null && !a.value().isEmpty() ? a.value() : f.getName();
    }
    public static String quote(String s) {
        StringBuilder b=new StringBuilder("\"");
        for (char c:s.toCharArray()) switch(c) {
            case '"' -> b.append("\\\""); case '\\' -> b.append("\\\\");
            case '\b' -> b.append("\\b"); case '\f' -> b.append("\\f");
            case '\n' -> b.append("\\n"); case '\r' -> b.append("\\r"); case '\t' -> b.append("\\t");
            default -> { if (c<32) b.append(String.format("\\u%04x",(int)c)); else b.append(c); }
        }
        return b.append('"').toString();
    }
    private String write(Object v, Set<Object> seen) {
        if (v==null) return "null";
        if (v instanceof Enum<?> e) return quote(e.name());
        if (v instanceof String || v instanceof Character) return quote(v.toString());
        if (v instanceof Boolean) return v.toString();
        if (v instanceof Number n) {
            if ((n instanceof Double && !Double.isFinite(n.doubleValue())) || (n instanceof Float && !Float.isFinite(n.floatValue())))
                throw new IllegalArgumentException("Nombre non fini");
            return n.toString();
        }
        if (!seen.add(v)) throw new IllegalArgumentException("Référence cyclique");
        try {
            StringJoiner j;
            if (v instanceof Iterable<?> list) {
                j=new StringJoiner(",","[","]"); for (Object x:list) j.add(write(x,seen));
            } else if (v.getClass().isArray()) {
                j=new StringJoiner(",","[","]"); for(int i=0;i<Array.getLength(v);i++) j.add(write(Array.get(v,i),seen));
            } else {
                j=new StringJoiner(",","{","}");
                Set<String> keys=new HashSet<>();
                for (Field f:fields(v.getClass())) {
                    if (!keys.add(name(f))) throw new IllegalArgumentException("Alias dupliqué");
                    j.add(quote(name(f))+":"+write(f.get(v),seen));
                }
            }
            return j.toString();
        } catch(IllegalAccessException e) { throw new IllegalArgumentException("Champ inaccessible",e); }
        finally { seen.remove(v); }
    }
    private Object convert(Object v, Type type) {
        if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> raw && List.class.isAssignableFrom(raw)) {
            if (v==null) return null;
            if (!(v instanceof List<?> list)) throw new IllegalArgumentException("Tableau attendu");
            List<Object> out=new ArrayList<>(); for(Object x:list) out.add(convert(x,p.getActualTypeArguments()[0])); return out;
        }
        if (!(type instanceof Class<?> c)) throw new IllegalArgumentException("Type non pris en charge : "+type);
        if (v==null) { if(c.isPrimitive()) throw new IllegalArgumentException("null pour un primitif"); return null; }
        if (c==Object.class) return v;
        if (c==String.class && v instanceof String) return v;
        if ((c==boolean.class || c==Boolean.class) && v instanceof Boolean) return v;
        if ((c==char.class || c==Character.class) && v instanceof String s && s.length()==1) return s.charAt(0);
        if (v instanceof BigDecimal n) {
            if(c==int.class || c==Integer.class) return n.intValueExact();
            if(c==long.class || c==Long.class) return n.longValueExact();
            if(c==short.class || c==Short.class) return n.shortValueExact();
            if(c==byte.class || c==Byte.class) return n.byteValueExact();
            if(c==double.class || c==Double.class) { double d=n.doubleValue(); if(!Double.isFinite(d)) throw new IllegalArgumentException("Nombre trop grand"); return d; }
            if(c==float.class || c==Float.class) { float f=n.floatValue(); if(!Float.isFinite(f)) throw new IllegalArgumentException("Nombre trop grand"); return f; }
            if(c==BigDecimal.class) return n;
        }
        if(c.isEnum() && v instanceof String s) {
            for(Object e:c.getEnumConstants()) if(((Enum<?>)e).name().equals(s)) return e;
            throw new IllegalArgumentException("Valeur d'énumération inconnue");
        }
        if(c.isArray() && v instanceof List<?> list) {
            Object arr=Array.newInstance(c.componentType(),list.size());
            for(int i=0;i<list.size();i++) Array.set(arr,i,convert(list.get(i),c.componentType())); return arr;
        }
        if(List.class.isAssignableFrom(c) && v instanceof List<?>) return v;
        if (!(v instanceof Map<?,?> map)) throw new IllegalArgumentException("Type JSON incompatible avec "+c.getName());
        try {
            Constructor<?> constructor=c.getDeclaredConstructor(); constructor.setAccessible(true);
            Object out=constructor.newInstance();
            for(Field f:fields(c)) if(map.containsKey(name(f))) f.set(out,convert(map.get(name(f)),f.getGenericType()));
            return out;
        } catch(ReflectiveOperationException e) { throw new IllegalArgumentException("Constructeur sans argument nécessaire : "+c.getName(),e); }
    }
    private static class Parser {
        final String s; int i;
        Parser(String s){this.s=s;}
        IllegalArgumentException error(String m){return new IllegalArgumentException(m+" à la position "+i);}
        void space(){while(i<s.length() && " \t\r\n".indexOf(s.charAt(i))>=0)i++;}
        boolean take(char c){space();if(i<s.length() && s.charAt(i)==c){i++;return true;}return false;}
        void need(char c){if(!take(c))throw error("Attendu : "+c);}
        Object value(int depth) {
            if(depth>100)throw error("JSON trop imbriqué");
            space();if(i>=s.length())throw error("Valeur manquante");char c=s.charAt(i);
            if(c=='"')return string();
            if(c=='{') {
                i++;Map<String,Object> m=new LinkedHashMap<>();if(take('}'))return m;
                do {space();if(i>=s.length()||s.charAt(i)!='"')throw error("Clé attendue");String k=string();need(':');
                    if(m.containsKey(k))throw error("Clé dupliquée");m.put(k,value(depth+1));}while(take(','));need('}');return m;
            }
            if(c=='['){i++;List<Object> l=new ArrayList<>();if(take(']'))return l;do{l.add(value(depth+1));}while(take(','));need(']');return l;}
            for(String literal:new String[]{"true","false","null"})if(s.startsWith(literal,i)){i+=literal.length();return literal.equals("null")?null:literal.equals("true");}
            int start=i;
            if(c=='-')i++;
            if(i>=s.length())throw error("Nombre incomplet");
            if(s.charAt(i)=='0')i++;else{if(s.charAt(i)<'1'||s.charAt(i)>'9')throw error("Nombre attendu");while(i<s.length()&&digit(s.charAt(i)))i++;}
            if(i<s.length()&&s.charAt(i)=='.'){i++;int pos=i;while(i<s.length()&&digit(s.charAt(i)))i++;if(i==pos)throw error("Fraction manquante");}
            if(i<s.length()&&(s.charAt(i)=='e'||s.charAt(i)=='E')){i++;if(i<s.length()&&(s.charAt(i)=='+'||s.charAt(i)=='-'))i++;int pos=i;while(i<s.length()&&digit(s.charAt(i)))i++;if(i==pos)throw error("Exposant manquant");}
            try{return new BigDecimal(s.substring(start,i));}catch(NumberFormatException e){throw error("Nombre invalide");}
        }
        static boolean digit(char c){return c>='0'&&c<='9';}
        String string(){need('"');StringBuilder b=new StringBuilder();while(i<s.length()) {
            char c=s.charAt(i++);if(c=='"')return b.toString();if(c<32)throw error("Contrôle non échappé");
            if(c!='\\'){b.append(c);continue;}if(i>=s.length())throw error("Échappement incomplet");
            switch(s.charAt(i++)) {
                case '"' -> b.append('"');case '\\' -> b.append('\\');case '/' -> b.append('/');
                case 'b' -> b.append('\b');case 'f' -> b.append('\f');case 'n' -> b.append('\n');case 'r' -> b.append('\r');case 't' -> b.append('\t');
                case 'u' -> {if(i+4>s.length())throw error("Unicode incomplet");try{String hex=s.substring(i,i+4);if(!hex.matches("[0-9a-fA-F]{4}"))throw error("Unicode invalide");b.append((char)Integer.parseInt(hex,16));}catch(NumberFormatException e){throw error("Unicode invalide");}i+=4;}
                default -> throw error("Échappement invalide");
            }
        }throw error("Chaîne non terminée");}
    }
}
