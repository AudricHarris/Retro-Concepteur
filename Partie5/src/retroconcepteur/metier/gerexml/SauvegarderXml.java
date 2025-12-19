package retroconcepteur.metier.gerexml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import retroconcepteur.metier.classe.Attribut;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.metier.classe.Liaison;
import retroconcepteur.metier.classe.Methode;
import retroconcepteur.metier.classe.Parametre;
import retroconcepteur.metier.classe.Position;

public class SauvegarderXml 
{
		public static void sauvegarderXml(String chemin, ArrayList<Classe> lstClasses, 
									  HashMap<Classe, Position> mapPos,
									  List<Liaison> lstLiaisons)
	{
		//variable
		DocumentBuilderFactory docFac;
		DocumentBuilder docConstru;
		Document doc;

		//element Racine
		Element elmRacine;

		//sous element -> ajouter dans racine
		Element classeElm;
		Element attributElm;
		Element methodeElm;

		try
		{
			docFac = DocumentBuilderFactory.newInstance();
			docConstru = docFac.newDocumentBuilder();

			doc = docConstru.newDocument();
			elmRacine = doc.createElement("Diagramme");
			doc.appendChild(elmRacine);

			for (Classe c : lstClasses)
			{
				classeElm = doc.createElement("Classe");

				classeElm.setAttribute("nom", c.getNom());
				classeElm.setAttribute("isAbstract", Boolean.toString(c.isAbstract()));
				classeElm.setAttribute("isInterface", Boolean.toString(c.isInterface()));

				if (c.getNomHeritageClasse() != null)
					classeElm.setAttribute("extends", c.getNomHeritageClasse());

				// gere interfaces
				if (c.getLstInterfaces() != null && !c.getLstInterfaces().isEmpty())
				{
					classeElm.appendChild(SauvegarderXml.classeInterface(doc,c));
				}

				SauvegarderXml.ajouterPosition(classeElm, mapPos.get(c));

				// gere Attributs
				attributElm = doc.createElement("Attributs");
				for (Attribut a : c.getLstAttribut()) 
				{
					attributElm.appendChild(SauvegarderXml.classeAttribut(doc, a));
				}
				classeElm.appendChild(attributElm);

				// gere Methodes
				methodeElm = doc.createElement("Methodes");
				for (Methode m : c.getLstMethode()) 
				{
					methodeElm.appendChild(SauvegarderXml.classeMethode(doc, m));
					classeElm.appendChild(methodeElm);
				}

				elmRacine.appendChild(classeElm);
			}

			// Sauvegarde des liaisons si fournies
			if (lstLiaisons != null && !lstLiaisons.isEmpty())
			{
				elmRacine.appendChild(sauvegarderLiaison(doc,lstLiaisons));
			}

			// ecrit dans doc xml
			SauvegarderXml.ecritXml(doc,chemin);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors de la sauvegarde XML : " + e.getMessage());
		}
	}

	//////////////////////////////////////////////////////
	/// Methodes utiliser pour la sauvegarde /////////////
	//////////////////////////////////////////////////////

	private static void ajouterPosition(Element classeElm, Position p)
	{
		if (p == null) return;

		classeElm.setAttribute("x", String.valueOf(p.getX()));
		classeElm.setAttribute("y", String.valueOf(p.getY()));
		classeElm.setAttribute("largeur", String.valueOf(p.getLargeur()));
		classeElm.setAttribute("hauteur", String.valueOf(p.getHauteur()));
	}

	private static Element classeMethode(Document doc, Methode m)
	{
		Element paramsElm;

		Element metElm;
		Element prmElm;

		metElm = doc.createElement("Methode");

		metElm.setAttribute("nom", m.getNom());
		metElm.setAttribute("type", m.getType());
		metElm.setAttribute("visibilite", m.getVisibilite() == null ? "" : m.getVisibilite());
		metElm.setAttribute("static", Boolean.toString(m.isStatic()));

		// gere Parametres
		paramsElm = doc.createElement("Parametres");
		for (Parametre p : m.getLstParam()) 
		{
			prmElm = doc.createElement("Parametre");

			prmElm.setAttribute("nom", p.getNom());
			prmElm.setAttribute("type", p.getType());

			paramsElm.appendChild(prmElm);
		}
		metElm.appendChild(paramsElm);

		return metElm;
	}

	private static Element classeAttribut(Document doc, Attribut a)
	{
		Element attElm;

		attElm = doc.createElement("Attribut");

		attElm.setAttribute("nom", a.getNom());
		attElm.setAttribute("type", a.getType());
		attElm.setAttribute("visibilite", a.getVisibilite() == null ? "" : a.getVisibilite());
		attElm.setAttribute("constante", Boolean.toString(a.isConstante()));
		attElm.setAttribute("static", Boolean.toString(a.isStatic()));
		attElm.setAttribute("addOnly", Boolean.toString(a.isAddOnly()));

		return attElm;
	}

	private static Element classeInterface(Document doc, Classe c)
	{
		Element interfaceElm;
		Element interElm;

		interfaceElm = doc.createElement("Interfaces");
		for (String inter : c.getLstInterfaces())
		{
			interElm = doc.createElement("Interface");

			interElm.setAttribute("nom", inter);

			interfaceElm.appendChild(interElm);
		}
		return interfaceElm;
	}

	private static Element sauvegarderLiaison(Document doc,List<Liaison>lstLiaisons)
	{
		Element liaisonsElm;
		Element lienElm;

		liaisonsElm = doc.createElement("Liaisons");
		for (Liaison l : lstLiaisons)
		{
			lienElm = doc.createElement("Liaison");

			lienElm.setAttribute("de", l.getFromClass().getNom());
			lienElm.setAttribute("vers", l.getToClass().getNom());
			lienElm.setAttribute("type", l.getType());
			lienElm.setAttribute("nomVar", l.getNomVar() == null ? "" : l.getNomVar());

			if (l.getFromMultiplicity() != null)
			{
				lienElm.setAttribute("deMin", l.getFromMultiplicity().getBorneInf());
				lienElm.setAttribute("deMax", l.getFromMultiplicity().getBorneSup());
			}
			if (l.getToMultiplicity() != null)
			{
				lienElm.setAttribute("versMin", l.getToMultiplicity().getBorneInf());
				lienElm.setAttribute("versMax", l.getToMultiplicity().getBorneSup());
			}
			liaisonsElm.appendChild(lienElm);
		}
		return liaisonsElm;
	}

	private static void ecritXml(Document doc, String chemin)
	{
		TransformerFactory transformerFactory;
		Transformer transformer;

		DOMSource source;

		StreamResult result;

		try 
		{
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			source = new DOMSource(doc);
			result = new StreamResult(new File(chemin));

			transformer.transform(source, result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors de la sauvegarde XML : " + e.getMessage());
		}
	}
}
