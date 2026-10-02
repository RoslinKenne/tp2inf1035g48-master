import com.company.Json1035;
import com.company.DTO.*;
import java.util.*;
public class JsonTest {
    public static class Scalars {public int count;public boolean active;public double value;public String text;public Scalars(){}}
    static void check(boolean b){if(!b)throw new AssertionError();}
    public static void main(String[] args) {
        var j=new Json1035();
        var p=new Person("Roslin \"Kenne\"",24,new Address("Trois-Rivières, QC","G9A 1A1"),Arrays.asList("jeux, cartes","a:b [c]","ligne\nsuivante",null));
        String json=j.serialize(p);var copy=j.deserialize(json,Person.class);
        check(copy.getName().equals(p.getName()));check(copy.getAddress().getCity().equals(p.getAddress().getCity()));check(copy.getHobbies().equals(p.getHobbies()));check(copy.getAge()==0);
        check(json.contains("ville de residence") && !json.contains("age"));
        Scalars s=j.deserialize("{\"count\":42,\"active\":true,\"value\":1.5e2,\"text\":\"\\u00e9\\t\\\\\"}",Scalars.class);
        check(s.count==42 && s.active && s.value==150 && s.text.equals("é\t\\"));
        check(j.serialize(s).contains("\"count\":42"));
        check(j.deserialize("null",Address.class)==null);
        check(Arrays.equals(j.deserialize("[1,2,3]",int[].class),new int[]{1,2,3}));
        for(String bad:new String[]{"", "{", "{\"count\":01}","{\"count\":1.5}","{\"text\":\"bad\\q\"}","{\"text\":\"\\u+123\"}","[1,]","true junk","{\"count\":1,\"count\":2}","{\"count\":null}"}) {
            try {j.deserialize(bad,Scalars.class);throw new AssertionError("Accepté : "+bad);}catch(IllegalArgumentException|ArithmeticException expected){}
        }
        var list=new ArrayList<Object>();list.add(list);try{j.serialize(list);throw new AssertionError();}catch(IllegalArgumentException expected){}
        System.out.println("JSON : round-trip, annotations, listes, types, erreurs et cycles OK");
    }
}
