package vue;

import java.util.ArrayList;
import metier.classe.Classe;
import metier.classe.Methode;
import metier.classe.Attribut;
import metier.classe.Parametre;

public class AffichageCUI 
{
	private final static int TAILLE_MIN = 48;

	public String afficherClasse (ArrayList<Classe> lstClasse)
	{   
		String sRet = "";

		for (Classe classe : lstClasse) 
		{
			int maxLargeur = TAILLE_MIN;
			String blocAttributs = "";
			String blocMethodes  = "";
			
			int tailleAttributMax = classe.getPlusGrandAttribut() + 1;
			int tailleMethodeMax  = classe.getPlusGrandeMethode() + 1;

			for (Attribut att : classe.getLstAttribut())
			{
				String ligne = "";
				int indiceStatique = 0;

				if (att.isStatic())
					indiceStatique = att.getNom().length();
				
				ligne += AffichageCUI.getSigneVisibilite(att.getVisibilite()) + " ";
				ligne += String.format("%-" + tailleAttributMax + "s", " " + att.getNom()) + " : " + att.getType();
				
				if (att.isConstante())
					ligne += "<<freeze>>";
				
				if (ligne.length() > maxLargeur) 
					maxLargeur = ligne.length();

				blocAttributs += ligne + "\n";

				if (indiceStatique > 0)
					blocAttributs += "   " + "\u203E".repeat(indiceStatique) + "\n";
			}

			for (Methode meth : classe.getLstMethode())
			{
				if (meth.getNom().equals("main")) 
					continue;

				String ligne = "";
				int indiceStatique = 0;

				if (meth.isStatic())
					indiceStatique = meth.getNom().length();

				ligne += AffichageCUI.getSigneVisibilite(meth.getVisibilite()) + " " ;
				
				String signature = meth.getNom() + "(";
				String params = "";
				for (Parametre p : meth.getLstParam())
					params += p.getNom() + ":" + p.getType() + ",";

				if (params.length() > 0)
					params = params.substring(0, params.length() - 1);

				signature += params + ")";

				ligne += String.format("%-" + tailleMethodeMax + "s", signature);

				if (!meth.getType().equals("void") && !meth.getNom().equals(classe.getNom())) 
					ligne += " : " + meth.getType();


				if (ligne.length() > maxLargeur) 
					maxLargeur = ligne.length();

				blocMethodes += ligne + "\n";

				if (indiceStatique > 0)
					blocMethodes += "   " + "\u203E".repeat(indiceStatique) + "\n";
			}

			if (classe.getNom().length() + 4 > maxLargeur) 
				maxLargeur = classe.getNom().length() + 4;

			String separateur = "-".repeat(maxLargeur);

			sRet += separateur + "\n";
			
			int decalage = (maxLargeur - classe.getNom().length()) / 2;
			sRet += String.format("%" + decalage + "s", "") + classe.getNom() + "\n";
			sRet += separateur + "\n";

			sRet += blocAttributs;
			sRet += separateur + "\n";

			sRet += blocMethodes;
			sRet += separateur + "\n\n\n";
		}
		
		return sRet;
	}

	private static char getSigneVisibilite(String visibilite)
	{
		switch (visibilite) 
		{
			case "public": return '+';
			case "private" : return '-';
			case "protected" : return '#'; 
			default: return '~';
		}
	}
}