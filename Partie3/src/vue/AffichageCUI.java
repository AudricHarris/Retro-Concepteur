package vue;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import metier.classe.Classe;
import metier.classe.Liaison;
import metier.classe.Methode;
import metier.classe.Attribut;
import metier.classe.Parametre;

import controller.Controller;

public class AffichageCUI
{
	private final static int TAILLE_MIN = 48;

	Controller ctrl;
	
	public AffichageCUI(Controller controller)
	{
		this.ctrl = controller;
	}

	public String afficherClasse ()
	{
		ArrayList<Classe> lstClasse = this.ctrl.getLstClasses();
		String sRet = "";

		for (Classe classe : lstClasse)
		{
			int maxLargeur = TAILLE_MIN;
			String blocAttributs = "";
			String blocMethodes = "";
			int tailleAttributMax = classe.getPlusGrandAttribut() + 1;
			int tailleMethodeMax = classe.getPlusGrandeMethode() + 1;
			String separateur = "";

			blocAttributs = this.getBlocAttribut(classe, tailleAttributMax );
			blocMethodes = this.getBlocMethode(classe, tailleMethodeMax );

			maxLargeur = this.getTailleSeparateur(blocAttributs, blocMethodes);

			if (classe.getNom().length() + 4 > maxLargeur)
				maxLargeur = classe.getNom().length() + 4;

			separateur = "-".repeat(maxLargeur);
			sRet += separateur + "\n";

			int decalage = (maxLargeur - classe.getNom().length()) / 2;

			sRet += (classe.isInterface() ? String.format("%" + (decalage-4) + "s", "") + "{ interface }" + "\n" : "");
			sRet += String.format("%" + decalage + "s", "") + classe.getNom() + "\n";
			sRet += (classe.isAbstract() ? String.format("%" + (decalage-3) + "s", "") + "{ abstract }" + "\n" : "");
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


	public String afficherLiaison()
	{
		String res = "";
		int numAssociation = 1;
		List<Liaison> listLiaison = this.ctrl.getListLiaison();
		for (Liaison l : listLiaison)
		{
			String nomFromClass = l.getFromClass().getNom();
			String nomToClass = l.getToClass().getNom();
			String multiplicity = l.getToMultiplicity().toString();
			res += "Association"+ numAssociation +" : Unidirectionnelle de " + nomFromClass + "(0..*) Vers " + nomToClass + multiplicity + "\n";
			numAssociation++;
		}
		for (Liaison l : listLiaison)
		{
			String nomFromClass = l.getFromClass().getNom();
			String nomToClass = l.getToClass().getNom();
			String multiplicity = l.getToMultiplicity().toString();
			res += "Association "+ numAssociation + " :bidirectionnelle de " + nomFromClass + "(1..*) Vers " + nomToClass + multiplicity +"\n";
			numAssociation++;
		}
		return res;
	}

	public String getBlocAttribut  (Classe classe, int tailleAttributMax) 
	{
		String blocAttributs="";
		
		for (Attribut att : classe.getLstAttribut())
		{
			String ligne = "";
			int indiceStatique = 0;

			if (this.ctrl.estClasseProjet(att.getType())) continue;
			if (att.isStatic())
				indiceStatique = att.getNom().length();

			ligne += AffichageCUI.getSigneVisibilite(att.getVisibilite()) + " ";

			ligne += String.format("%-" + tailleAttributMax + "s", " " + att.getNom()) + " : " + att.getType();

			if (att.isConstante())
				ligne += "<<freeze>>";


			blocAttributs += ligne + "\n";

			if (indiceStatique > 0)
				blocAttributs += "   " + "\u203E".repeat(indiceStatique) + "\n";
		}

		return blocAttributs;
	}



	public String getBlocMethode (Classe classe, int tailleMethodeMax)
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

			blocMethodes += ligne + "\n";

			if (indiceStatique > 0)
				blocMethodes += "   " + "\u203E".repeat(indiceStatique) + "\n";
		}

		return blocMethodes;
	}


	public int getLigneMax(String bloc)
	{
		int max =0;
		int ligne;

		try 
		{
			Scanner scanner = new Scanner(bloc);
			scanner.useDelimiter("\\n");
			while (scanner.hasNextLine()) 
			{
				ligne = scanner.next().length();
				if ( ligne > max)
					max = ligne;
			}
			scanner.close();

		}catch(Exception e){}

		return max;
	}

	public int getTailleSeparateur(String blocMeth, String blocAtt)
	{
		return Math.max(this.getLigneMax(blocMeth), this.getLigneMax(blocAtt));
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
