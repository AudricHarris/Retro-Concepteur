import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
public abstract Test
*/
public class Test 
{

	//private String str;
/**
 * Classe de test pour les cas limites du Retro-Concepteur.
 * public class Test
 * Teste : Commentaires, Generics imbriqués, Modificateurs.
 */

    // Type très long sans espaces
    private ArrayList<HashMap<Integer,ArrayList<String>>> typeImbriqueSansEspace;

    public static final String CONSTANTE_GLOBALE = "TEST";

	double pourcentage;

    // ---------------------------------------------------------
    // 4. Constructeur et Méthodes (pour vérifier qu'ils ne sont pas pris pour des attributs)
    // ---------------------------------------------------------

    public Test() {
        this.typeImbriqueSansEspace = new ArrayList<>();ço
    }

	public void traiterDonnees(ArrayList<String> data) {
        System.out.println("public String toString()");
    }
}	

