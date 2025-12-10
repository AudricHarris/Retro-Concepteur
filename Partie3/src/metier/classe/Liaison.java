package metier.classe;
import controller.Controller;
import metier.AnalyseFichier;

import java.util.ArrayList;
import java.util.List;

public class Liaison 
{

    private String fromClass;
    private String toClass;
    private Multiplicite toMultiplicity;

	private Controller ctrl;
	private AnalyseFichier analyseFichier;

	public static List<Liaison> creerLiaison(Classe classe1 , Classe classe2)
	{
		List<Liaison> lstLiaisons = new ArrayList<Liaison>();
		Liaison liaison = null;
		for (Attribut attribut1 : classe1.getLstAttribut())
		{
			if (attribut1.getType().equals(classe2.getNom()       ) || 
			    attribut1.getType().contains(classe2.getNom()+">" ) ||
			    attribut1.getType().contains(classe2.getNom()+"[]")) 
			{
				liaison = new Liaison( classe1, classe2, attribut1);
				lstLiaisons.add(liaison);
			}
		}
		
		return lstLiaisons;
	}


	private Liaison(Classe classe1 , Classe classe2, Attribut attribut1)
	{
		this.toMultiplicity = new Multiplicite("temp","temp");

		this.fromClass =  classe1.getNom();
		this.toClass = classe2.getNom();
		
		this.toMultiplicity = new Multiplicite("0","temp");

		if (classe1.getLstMethode().size()>0) 
		{
		
			Methode constructeur =  classe1.getLstMethode().get(0);
			List<Parametre> params = constructeur.getLstParam();
			for (Parametre parametre : params) 
			{
				if (parametre.getType().contains(classe2.getNom()))
				{
					this.toMultiplicity = new Multiplicite("1","temp");
					break;
				}
				
			}
		}

		if (Liaison.estCollection(attribut1.getType()))
			this.toMultiplicity.setBorneSup("*");
		else
			this.toMultiplicity.setBorneSup("1");
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
		return this.fromClass + " ----> " + this.toMultiplicity.toString() + " " + this.toClass;
	}
	
}
