package retroconcepteur.metier.gerexml;

// Nos paquetage
import retroconcepteur.metier.classe.Attribut;
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.metier.classe.Liaison;
import retroconcepteur.metier.classe.Methode;
import retroconcepteur.metier.classe.Parametre;
import retroconcepteur.metier.classe.Position;

// paquetage io
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// paquetage util
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// paquetage org
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Classe utilitaire permettant de sauvegarder un diagramme de classes
 * sous forme de fichier XML.
 */
public class SauvegarderXml 
{
	/*------------------------------------------*/
	/*       Methode de classe public           */
	/*------------------------------------------*/

	/**
	 * Sauvegarde un diagramme de classes dans un fichier XML.
	 *
	 * @param chemin chemin du fichier XML à créer
	 * @param lstClasses liste des classes du diagramme
	 * @param mapPos association entre chaque classe et sa position graphique
	 * @param lstLiaisons liste des liaisons entre les classes
	 * @throws RuntimeException si une erreur survient lors de la génération du XML
	 */
	public static void sauvegarderEnXml(String chemin, ArrayList<Classe> lstClasses,
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
				classeElm.setAttribute("cachable", Boolean.toString(c.getCachable()));

				if (c.getNomHeritageClasse() != null)
					classeElm.setAttribute("extends", c.getNomHeritageClasse());

				if (c.getLstInterfaces() != null && !c.getLstInterfaces().isEmpty())
				{
					classeElm.appendChild(SauvegarderXml.classeInterface(doc,c));
				}

				SauvegarderXml.ajouterPosition(classeElm, mapPos.get(c));

				attributElm = doc.createElement("Attributs");
				for (Attribut a : c.getLstAttribut()) 
				{
					attributElm.appendChild(SauvegarderXml.classeAttribut(doc, a));
				}
				classeElm.appendChild(attributElm);

				methodeElm = doc.createElement("Methodes");
				for (Methode m : c.getLstMethode()) 
				{
					methodeElm.appendChild(SauvegarderXml.classeMethode(doc, m));
					classeElm.appendChild(methodeElm);
				}

				elmRacine.appendChild(classeElm);
			}

			if (lstLiaisons != null && !lstLiaisons.isEmpty())
			{
				elmRacine.appendChild(sauvegarderLiaison(doc,lstLiaisons));
			}

			SauvegarderXml.ecritXml(doc,chemin);
		} 
		catch (Exception e)
		{
			throw new RuntimeException("Erreur lors de la sauvegarde XML : " + e.getMessage());
		}
	}

	/*------------------------------------------*/
	/*      Methode de classe privee            */
	/*------------------------------------------*/

	/**
	 * Ajoute les informations de position graphique à un élément XML représentant une classe.
	 *
	 * @param classeElm élément XML de la classe
	 * @param p position graphique de la classe
	 */
	private static void ajouterPosition(Element classeElm, Position p)
	{
		if (p == null) return;

		classeElm.setAttribute("x", String.valueOf(p.getX()));
		classeElm.setAttribute("y", String.valueOf(p.getY()));
		classeElm.setAttribute("largeur", String.valueOf(p.getLargeur()));
		classeElm.setAttribute("hauteur", String.valueOf(p.getHauteur()));
	}

	/**
	 * Crée un élément XML représentant une méthode.
	 *
	 * @param doc document XML
	 * @param m méthode à convertir en XML
	 * @return élément XML correspondant à la méthode
	 */
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

	/**
	 * Crée un élément XML représentant un attribut.
	 *
	 * @param doc document XML
	 * @param a attribut à convertir en XML
	 * @return élément XML correspondant à l'attribut
	 */
	private static Element classeAttribut(Document doc, Attribut a)
	{
		Element attElm;

		attElm = doc.createElement("Attribut");

		attElm.setAttribute("nom", a.getNom());
		attElm.setAttribute("type", a.getType());
		attElm.setAttribute("visibilite", a.getVisibilite() == null ? "" : a.getVisibilite());
		attElm.setAttribute("constante", Boolean.toString(a.isConstante()));
		attElm.setAttribute("static", Boolean.toString(a.isStatic()));

		return attElm;
	}

	/**
	 * Crée un élément XML représentant les interfaces implémentées par une classe.
	 *
	 * @param doc document XML
	 * @param c classe concernée
	 * @return élément XML contenant les interfaces
	 */
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

	/**
	 * Crée un élément XML représentant l'ensemble des liaisons du diagramme.
	 *
	 * @param doc document XML
	 * @param lstLiaisons liste des liaisons
	 * @return élément XML contenant les liaisons
	 */
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

	/**
	 * Écrit le document XML sur le disque.
	 *
	 * @param doc document XML à écrire
	 * @param chemin chemin du fichier de sortie
	 * @throws RuntimeException si une erreur survient lors de l'écriture
	 */
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
			throw new RuntimeException("Erreur lors de la sauvegarde XML : " + e.getMessage());
		}
	}
}