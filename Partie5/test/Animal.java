public class Animal implements IAnimal
{
    
    
    // Une constante (public static final par defaut)
    public static final String ESPECE_GENERIQUE = "Vertebre";
    
    // Une methode abstraite (public abstract par defaut)
    public void crier()
    {
        System.out.println("djezghjgfkzbrfm");
    } 
    
    // Une autre methode abstraite
    public void seDeplacer(String destination)
    {
        System.out.println("fhfh");
    }
    
    // a partir de Java 8, vous pouvez avoir des methodes par defaut (default methods)
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
