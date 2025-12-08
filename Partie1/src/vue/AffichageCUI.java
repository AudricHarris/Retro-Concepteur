package vue;

import java.util.ArrayList;

import metier.classe.Classe;

public class AffichageCUI 
{
	
	public void afficherClasse (ArrayList<Classe> lstClasse)
	{
		for (Classe classe : lstClasse) 
			System.out.println(classe.toString());
		
	}
}	
