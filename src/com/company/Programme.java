package com.company;

import com.company.DTO.Person;
import com.company.DTO.Address;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Programme {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Json1035 json1035 = new Json1035(); // Instance de Json1035

        int choix;
        do {
            afficherMenuPrincipal();
            choix = lireEntreeEntier("Entrez votre choix : ");
            try {
            switch (choix) {
                case 1 -> serialiser(json1035);
                case 2 -> deserialiser(json1035);
                case 3 -> System.out.println("Au revoir!");
                default -> System.out.println("Choix invalide. Veuillez réessayer.");
            }
            } catch (IllegalArgumentException e) { System.out.println("JSON invalide : " + e.getMessage()); }
        } while (choix != 3);
    }

    private static void afficherMenuPrincipal() {
        System.out.println("---------------- menu principal de mon serialiseur------------" +
                                                 "que voulez vous faire ");
        System.out.println("1. Sérialiser un objet en JSON");
        System.out.println("2. Désérialiser un JSON en objet");
        System.out.println("3. Quitter");
    }

    private static void serialiser(Json1035 json1035) {
        System.out.println("===== Choix de l'objet à créer =====");
        System.out.println("1. Personne");
        System.out.println("2. Adresse");

        int choix = lireEntreeEntier("Entrez votre choix : ");

        Object obj = null;
        switch (choix) {
            case 1 -> obj = creerPersonne();
            case 2 -> obj = creerAdresse();
            default -> {
                System.out.println("Choix invalide. Retour au menu principal.");
                return;
            }
        }

        if (obj != null) {
            String json = json1035.serialize(obj); // implemente dans Json1035
            System.out.println("Objet sérialisé en JSON :\n" + json);
        }
    }

    private static void deserialiser(Json1035 json1035) {
        System.out.println("===== Choix de l'objet à désérialiser =====");
        System.out.println("1. Personne");
        System.out.println("2. Adresse");

        int choix = lireEntreeEntier("Entrer votre choix  ");

        System.out.println("Entrez la chaîne JSON à désérialiser : ");
        String json = scanner.nextLine();

        switch (choix) {
            case 1 -> {
                Person person = json1035.deserialize(json, Person.class); // implemente dans Json1035
                afficherPersonne(person);
            }
            case 2 -> {
                Address address = json1035.deserialize(json, Address.class); // implemente dans Json1035
                afficherAdresse(address);
            }
            default -> System.out.println("Choix invalide. Retour au menu principal.");
        }
    }

    private static Person creerPersonne() {
        System.out.print("Entrez le nom : ");
        String name = scanner.nextLine();

        int age = lireEntreeEntier("Entrez l'âge : ");

        System.out.print("Entrez les hobbies (separes par des virgules) : ");
        List<String> hobbies = Arrays.asList(scanner.nextLine().split(","));

        Address address = creerAdresse();

        return new Person(name, age, address, hobbies);
    }

    private static Address creerAdresse() {
        System.out.print("Entrez la ville : ");
        String city = scanner.nextLine();

        System.out.print("Entrez le code postal : ");
        String postalCode = scanner.nextLine();

        return new Address(city, postalCode);
    }

    private static void afficherPersonne(Person person) {
        System.out.println(" Detailde la Personne ");
        System.out.println("Nom : " + person.getName());
        System.out.println("Âge : " + person.getAge());
        System.out.println("Hobbies : " + (person.getHobbies() == null ? "" : String.join(", ", person.getHobbies())));
        if (person.getAddress() != null) afficherAdresse(person.getAddress());
    }

    private static void afficherAdresse(Address address) {
        System.out.println("===== Détails de l'Adresse =====");
        System.out.println("Ville : " + address.getCity());
        System.out.println("Code postal : " + address.getPostalCode());
    }

    private static int lireEntreeEntier(String message) {
        int result = -1;
        boolean valide = false;
        while (!valide) {
            try {
                System.out.print(message);
                if (!scanner.hasNextLine()) return 3;
                result = Integer.parseInt(scanner.nextLine());
                valide = true;
            } catch (NumberFormatException e) {
                System.out.println("Entrée non valide, veuillez entrer un nombre.");
            }
        }
        return result;
    }
}
