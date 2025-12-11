package vue;
import java.util.ArrayList;
import java.util.List;
import metier.classe.Classe;
import metier.classe.Liaison;
import metier.classe.Methode;
import metier.AnalyseFichier;
import metier.classe.Attribut;
import metier.classe.Parametre;
public class AffichageCUI
{
	private final static int TAILLE_MIN = 48;
	public String afficherClasse (AnalyseFichier analyseFichier)
	{
		ArrayList<Classe> lstClasse = analyseFichier.getLstClasses();
		String sRet = "";
		for (Classe classe : lstClasse)
		{
			int maxLargeur = TAILLE_MIN;
			String blocAttributs = "";
			String blocMethodes = "";
			int tailleAttributMax = classe.getPlusGrandAttribut() + 1;
			int tailleMethodeMax = classe.getPlusGrandeMethode() + 1;
			String separateur = "";

			blocAttributs = this.getBlocAttribut(classe, tailleAttributMax, maxLargeur, analyseFichier );
			blocMethodes = this.getBlocMethode(classe, tailleMethodeMax, maxLargeur);


			if (classe.getNom().length() + 4 > maxLargeur)
				maxLargeur = classe.getNom().length() + 4;

			separateur = "-".repeat(maxLargeur);
			sRet += separateur + "\n";

			int decalage = (maxLargeur - classe.getNom().length()) / 2;

			sRet += (classe.getIsInterface() ? String.format("%" + (decalage-4) + "s", "") + "{ interface }" + "\n" : "");
			sRet += String.format("%" + decalage + "s", "") + classe.getNom() + "\n";
			sRet += (classe.getIsAbstract() ? String.format("%" + (decalage-3) + "s", "") + "{ abstract }" + "\n" : "");
			sRet += separateur + "\n";

			sRet += blocAttributs;
			sRet += separateur + "\n";
			sRet += blocMethodes;
			sRet += separateur + "\n\n\n";

			List<String> lstInterfaces = classe.getLstInterfaces();
			String heritage = classe.getHeritageClasse() != null ? classe.getHeritageClasse().getNom() : null;
			if (heritage != null)
				sRet += classe.getNom() + " hérite de " + heritage + "\n";

			if (lstInterfaces != null && !lstInterfaces.isEmpty())
			{
				sRet += classe.getNom() + " implémente : ";
				for (String interfaceNom : lstInterfaces)
					sRet += interfaceNom + ", ";
				sRet = sRet.substring(0, sRet.length() - 2);
				sRet += "\n";
			}
			sRet += "\n\n";
		}
		return sRet;
	}


	public String afficherLiaison(AnalyseFichier analyseFichier)
	{
		String res = "";
		int numAssociation = 1;
		for (Liaison l : analyseFichier.getListLiaisonUnique())
		{
			String nomFromClass = l.getFromClass().getNom();
			String nomToClass = l.getToClass().getNom();
			String multiplicity = l.getToMultiplicity().toString();
			res += "Association"+ numAssociation +" : Unidirectionnelle de " + nomFromClass + "(0..*) Vers " + nomToClass + multiplicity + "\n";
			numAssociation++;
		}
		for (Liaison l : analyseFichier.getListLiaisonBinaire())
		{
			String nomFromClass = l.getFromClass().getNom();
			String nomToClass = l.getToClass().getNom();
			String multiplicity = l.getToMultiplicity().toString();
			res += "Association "+ numAssociation + " :bidirectionnelle de " + nomFromClass + "(1..*) Vers " + nomToClass + multiplicity +"\n";
			numAssociation++;
		}
		return res;
	}

	public String getBlocAttribut  (Classe classe, int tailleAttributMax, int maxLargeur, AnalyseFichier analyseFichier) 
	{
		String blocAttributs="";
		
		for (Attribut att : classe.getLstAttribut())
		{
			String ligne = "";
			int indiceStatique = 0;
			// MODIF ICI : utilise la nouvelle méthode pour skipper si c'est une classe du projet (directe, générique ou tableau)
			if (analyseFichier.refersToProjectClass(att.getType())) continue;
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
				blocAttributs += " " + "\u203E".repeat(indiceStatique) + "\n";
		}

		return blocAttributs;
	}



	public String getBlocMethode (Classe classe, int tailleMethodeMax, int maxLargeur)
	{
		String blocMethodes ="";
		
		for (Methode meth : classe.getLstMethode())
		{
			String ligne = "";
			int indiceStatique = 0;
			String signature = meth.getNom() + "(";
			String params = "";
			
			if (meth.getNom().equals("main"))
				continue;
			
			if (meth.isStatic())
				indiceStatique = meth.getNom().length();

			ligne += AffichageCUI.getSigneVisibilite(meth.getVisibilite()) + " " ;
			
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

		return blocMethodes;
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
