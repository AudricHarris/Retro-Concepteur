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
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */

public class SauvegarderXml 
{
	/*------------------------------------------*/
	/*       Methode de classe public           */
	/*------------------------------------------*/

	/**
	 * Sauvegarde un diagramme de classes dans un fichier XML.
	 *
	 * @param chemin chemin du fichier XML a creer
	 * @param lstClasses liste des classes du diagramme
	 * @param mapPos association entre chaque classe et sa position graphique
	 * @param lstLiaisons liste des liaisons entre les classes
	 * @throws RuntimeException si une erreur survient lors de la generation du XML
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
				classeElm.setAttribute("estAbstract", Boolean.toString(c.estAbstract()));
				classeElm.setAttribute("estInterface", Boolean.toString(c.estInterface()));
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
	 * Ajoute les informations de position graphique a un element XML representant une classe.
	 *
	 * @param classeElm element XML de la classe
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
	 * Cree un element XML representant une methode.
	 *
	 * @param doc document XML
	 * @param m methode a convertir en XML
	 * @return element XML correspondant a la methode
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
		metElm.setAttribute("static", Boolean.toString(m.estStatic()));

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
	 * Cree un element XML representant un attribut.
	 *
	 * @param doc document XML
	 * @param a attribut a convertir en XML
	 * @return element XML correspondant a l'attribut
	 */
	private static Element classeAttribut(Document doc, Attribut a)
	{
		Element attElm;

		attElm = doc.createElement("Attribut");

		attElm.setAttribute("nom", a.getNom());
		attElm.setAttribute("type", a.getType());
		attElm.setAttribute("visibilite", a.getVisibilite() == null ? "" : a.getVisibilite());
		attElm.setAttribute("constante", Boolean.toString(a.isConstante()));
		attElm.setAttribute("static", Boolean.toString(a.estStatic()));

		return attElm;
	}

	/**
	 * Cree un element XML representant les interfaces implementees par une classe.
	 *
	 * @param doc document XML
	 * @param c classe concernee
	 * @return element XML contenant les interfaces
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
	 * Cree un element XML representant l'ensemble des liaisons du diagramme.
	 *
	 * @param doc document XML
	 * @param lstLiaisons liste des liaisons
	 * @return element XML contenant les liaisons
	 */
	private static Element sauvegarderLiaison(Document doc,List<Liaison>lstLiaisons)
	{
		Element liaisonsElm;
		Element lienElm;

		liaisonsElm = doc.createElement("Liaisons");
		for (Liaison l : lstLiaisons)
		{
			lienElm = doc.createElement("Liaison");

			lienElm.setAttribute("de", l.getClasseDep().getNom());
			lienElm.setAttribute("vers", l.getClasseArr().getNom());
			lienElm.setAttribute("type", l.getType());
			lienElm.setAttribute("nomVar", l.getNomVar() == null ? "" : l.getNomVar());

			if (l.getMultADep() != null)
			{
				lienElm.setAttribute("deMin", l.getMultADep().getBorneInf());
				lienElm.setAttribute("deMax", l.getMultADep().getBorneSup());
			}
			if (l.getMultArr() != null)
			{
				lienElm.setAttribute("versMin", l.getMultArr().getBorneInf());
				lienElm.setAttribute("versMax", l.getMultArr().getBorneSup());
			}
			liaisonsElm.appendChild(lienElm);
		}
		return liaisonsElm;
	}

	/**
	 * ecrit le document XML sur le disque.
	 *
	 * @param doc document XML a ecrire
	 * @param chemin chemin du fichier de sortie
	 * @throws RuntimeException si une erreur survient lors de l'ecriture
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