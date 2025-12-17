public class Animal implements IAnimal
{
    
    
    // Une constante (public static final par défaut)
    public static final String ESPECE_GENERIQUE = "Vertébré";
    
    // Une méthode abstraite (public abstract par défaut)
    public void crier()
    {
        System.out.println("djezghjgfkzbrfm");
    } 
    
    // Une autre méthode abstraite
    public void seDeplacer(String destination)
    {
        System.out.println("fhfh");
    }
    
    // À partir de Java 8, vous pouvez avoir des méthodes par défaut (default methods)
    public void dormir() {
        System.out.println("L'animal dort paisiblement.");
    }

    @Override
    public String getNom() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNom'");
    }

    @Override
    public void setNom(String nom) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNom'");
    }

    @Override
    public void manger() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'manger'");
    }

    @Override
    public int age() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'age'");
    }
}
