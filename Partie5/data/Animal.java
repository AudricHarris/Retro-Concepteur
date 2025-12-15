// Fichier : Animal.java
public interface Animal {
    
    
    // Une constante (public static final par défaut)
    public static final String ESPECE_GENERIQUE = "Vertébré";
    
    // Une méthode abstraite (public abstract par défaut)
    void crier(); 
    
    // Une autre méthode abstraite
    void seDeplacer(String destination);
    
    // À partir de Java 8, vous pouvez avoir des méthodes par défaut (default methods)
    void dormir() {
        System.out.println("L'animal dort paisiblement.");
    }
}
