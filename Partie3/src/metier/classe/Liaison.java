package metier.classe;
import controller.Controller;
import metier.AnalyseFichier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Liaison 
{

    private String fromClass;
    private String toClass;
    private Multiplicite fromMultiplicity;
    private Multiplicite toMultiplicity;

	private Controller ctrl;
	private AnalyseFichier analyseFichier;

	public Liaison(Classe classe1 , Classe classe2)
	{
		for (Attribut attribut1 : classe1.getLstAttribut())
		{
			if (attribut1.type().equals(classe2.getNom())) 
			{
				this.fromClass =  classe1.getNom();
				this.toClass = classe2.getNom();
				Methode constructeur =  classe1.getLstMethode().get(0);
				List<Parametre> params = constructeur.lstParam();
				
				for (Parametre parametre : params) 
				{
					if (parametre.type().equals(classe2.getNom())) 
						this.toMultiplicity = new Multiplicite("1","temp");
					else
						this.toMultiplicity = new Multiplicite("0","temp");
					
				}

				if (Liaison.estCollection(attribut1.type()))
					this.toMultiplicity.setBorneSup("*");
				else
					this.toMultiplicity.setBorneSup("1");
			}
		}
	}
	private static boolean estCollection(String type) 
	{
        if (type == null) return false;
        String t = type.trim();

		
        if (t.contains("[]"))
            return true;

		
        return t.startsWith("List<")
            || t.startsWith("ArrayList<")
            || t.startsWith("LinkedList<")
            || t.startsWith("Set<")
            || t.startsWith("HashSet<")
            || t.startsWith("TreeSet<")
            || t.startsWith("Collection")
            || t.startsWith("Iterable<");
    }
	public String toString()
	{
		return this.fromClass + " " + this.fromMultiplicity.toString() + " ---- " + this.toMultiplicity.toString() + " " + this.toClass;
	}
	
}
