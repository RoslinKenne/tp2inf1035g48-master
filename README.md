# Sérialiseur et désérialiseur JSON en Java

Prérequis : JDK 21. Aucune bibliothèque externe.

Sous Bash :

```sh
mkdir -p build
shopt -s globstar
javac -d build src/**/*.java tests/*.java
java -cp build com.company.Programme
java -cp build JsonTest
```

Types pris en charge : objets avec constructeur sans argument, scalaires, null, listes typées et tableaux. `@Alias` change le nom JSON ; `@Ignored` exclut un champ des deux opérations. Les chaînes sont échappées, les nombres et booléens conservent leur type. Les clés inconnues sont ignorées ; les clés dupliquées et JSON malformés sont refusés. Les références cycliques sont refusées. Les Maps, records et objets Java sans constructeur sans argument ne font pas partie du périmètre.

Tests : aller-retour avec listes et objets imbriqués, caractères échappés, annotations, tableaux, valeurs nulles, nombres et données invalides.

## Validation

Les tests couvrent les scénarios décrits ci-dessous. Ils vérifient la version corrigée ; ils ne garantissent pas tous les usages possibles.
