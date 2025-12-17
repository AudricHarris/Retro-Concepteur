package RetroConcepteur.metier;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import RetroConcepteur.metier.classe.Attribut;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.metier.classe.Methode;
import RetroConcepteur.metier.classe.Multiplicite;
import RetroConcepteur.metier.classe.Parametre;

import RetroConcepteur.vue.outil.Rectangle;

public class GereXml 
{
	private GereXml() { }

	public static void sauvegarderXml(String chemin, ArrayList<Classe> lstClasses, 
									  HashMap<Classe, Rectangle> mapRect,
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
					classeElm.appendChild(GereXml.classeInterface(doc,c));
				}

				GereXml.ajouterPosition(classeElm, mapRect.get(c));

				// gere Attributs
				attributElm = doc.createElement("Attributs");
				for (Attribut a : c.getLstAttribut()) 
				{
					attributElm.appendChild(GereXml.classeAttribut(doc, a));
				}
				classeElm.appendChild(attributElm);

				// gere Methodes
				methodeElm = doc.createElement("Methodes");
				for (Methode m : c.getLstMethode()) 
				{
					methodeElm.appendChild(GereXml.classeMethode(doc, m));
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
			GereXml.ecritXml(doc,chemin);
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

	private static void ajouterPosition(Element classeElm, Rectangle r)
	{
		if (r == null) return;

		classeElm.setAttribute("x", String.valueOf(r.getX()));
		classeElm.setAttribute("y", String.valueOf(r.getY()));
		classeElm.setAttribute("largeur", String.valueOf(r.getTailleX()));
		classeElm.setAttribute("hauteur", String.valueOf(r.getTailleY()));
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

			lienElm.setAttribute("from", l.getFromClass().getNom());
			lienElm.setAttribute("to", l.getToClass().getNom());
			lienElm.setAttribute("type", l.getType());
			lienElm.setAttribute("nomVar", l.getNomVar() == null ? "" : l.getNomVar());

			if (l.getFromMultiplicity() != null)
			{
				lienElm.setAttribute("fromMin", l.getFromMultiplicity().getBorneInf());
				lienElm.setAttribute("fromMax", l.getFromMultiplicity().getBorneSup());
			}
			if (l.getToMultiplicity() != null)
			{
				lienElm.setAttribute("toMin", l.getToMultiplicity().getBorneInf());
				lienElm.setAttribute("toMax", l.getToMultiplicity().getBorneSup());
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

	/////////////////////////////////////////////////
	/////////Chargement//////////////////////////////
	/////////////////////////////////////////////////

	public static ArrayList<Classe> chargerClassesXml(String chemin)
	{
		// Variables
		File fichierXml;
		DocumentBuilderFactory docFact;
		DocumentBuilder docConstru;
		Document doc;

		NodeList lstNoeux;
		ArrayList<Classe> classes;

		try
		{
			fichierXml = new File(chemin);
			docFact = DocumentBuilderFactory.newInstance();
			docConstru = docFact.newDocumentBuilder();
			doc = docConstru.parse(fichierXml);
			doc.getDocumentElement().normalize();

			lstNoeux = doc.getElementsByTagName("Classe");
			classes = new ArrayList<>();

			for (int i = 0; i < lstNoeux.getLength(); i++)
			{
				Classe c = lireClasseXml(lstNoeux.item(i));
				if (c != null)
					classes.add(c);
			}

			return classes;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement XML : " + e.getMessage());
		}
	}

	private static Classe lireClasseXml(Node node)
	{
		Element elm;
		String nom;

		Classe c;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return null;

		elm = (Element) node;
		nom = elm.getAttribute("nom");
		c = new Classe(nom);

		lireProprietesClasse(elm, c);
		lireInterfaces(elm, c);
		lireAttributs(elm, c);
		lireMethodes(elm, c);

		return c;
	}

	private static void lireProprietesClasse(Element elm, Classe c)
	{
		String sAbstract;
		String sInterface;
		String sExtends;

		sAbstract = elm.getAttribute("isAbstract");
		if (!sAbstract.isEmpty())
			c.setIsAbstract(Boolean.parseBoolean(sAbstract));

		sInterface = elm.getAttribute("isInterface");
		if (!sInterface.isEmpty())
			c.setIsInterface(Boolean.parseBoolean(sInterface));

		sExtends = elm.getAttribute("extends");
		if (!sExtends.isEmpty())
			c.setNomHeritageClasse(sExtends);
	}

	private static void lireInterfaces(Element elm, Classe c)
	{
		NodeList interfacesNodes;
		Node in;
		Element interElm;
		String nom;

		interfacesNodes = elm.getElementsByTagName("Interface");

		for (int i = 0; i < interfacesNodes.getLength(); i++)
		{
			in = interfacesNodes.item(i);
			if (in.getNodeType() == Node.ELEMENT_NODE)
			{
				interElm = (Element) in;
				nom = interElm.getAttribute("nom");
				if (!nom.isEmpty())
					c.ajouterInterface(nom);
			}
		}
	}

	private static void lireAttributs(Element elm, Classe c)
	{
		NodeList atts;
		Node attNode;
		Element aEl;

		String nom;
		String type;
		String vis;
		boolean constante;
		boolean statique;

		atts = elm.getElementsByTagName("Attribut");

		for (int i = 0; i < atts.getLength(); i++)
		{
			attNode = atts.item(i);
			if (attNode.getNodeType() == Node.ELEMENT_NODE)
			{
				aEl = (Element) attNode;

				nom = aEl.getAttribute("nom");
				type = aEl.getAttribute("type");
				vis = aEl.getAttribute("visibilite");
				constante = Boolean.parseBoolean(aEl.getAttribute("constante"));
				statique = Boolean.parseBoolean(aEl.getAttribute("static"));

				c.ajouterAttribut(nom, constante, type, vis, statique);
			}
		}
	}

	private static void lireMethodes(Element elm, Classe c)
	{
		NodeList meths;
		Node mNode;
		Element mEl;

		String nom;
		String type;
		String vis;
		boolean statique;

		meths = elm.getElementsByTagName("Methode");

		for (int i = 0; i < meths.getLength(); i++)
		{
			mNode = meths.item(i);
			if (mNode.getNodeType() == Node.ELEMENT_NODE)
			{
				mEl = (Element) mNode;

				nom = mEl.getAttribute("nom");
				type = mEl.getAttribute("type");
				vis = mEl.getAttribute("visibilite");
				statique = Boolean.parseBoolean(mEl.getAttribute("static"));

				c.ajouterMethode(vis, nom, type, lireParametres(mEl), statique);
			}
		}
	}

	private static ArrayList<Parametre> lireParametres(Element mEl)
	{
		ArrayList<Parametre> params;
		NodeList paramsList;
		Node pNode;
		Element prmElm;

		String nom;
		String type;

		params = new ArrayList<>();
		paramsList = mEl.getElementsByTagName("Parametre");

		for (int i = 0; i < paramsList.getLength(); i++)
		{
			pNode = paramsList.item(i);
			if (pNode.getNodeType() == Node.ELEMENT_NODE)
			{
				prmElm = (Element) pNode;
				nom = prmElm.getAttribute("nom");
				type = prmElm.getAttribute("type");
				params.add(new Parametre(nom, type));
			}
		}

		return params;
	}

	//////////////////////////////////////////////////
	/// Chargement des positions et liaisons /////////////
	///////////////////////////////////////////////////


	public static HashMap<String, Rectangle> chargerPositionsXml(String chemin)
	{
		File fchXml;
		DocumentBuilderFactory docFact;
		DocumentBuilder docConstru;
		Document doc;

		NodeList lstNoeux;
		HashMap<String, Rectangle> mapRect;

		try
		{
			fchXml = new File(chemin);
			docFact = DocumentBuilderFactory.newInstance();
			docConstru = docFact.newDocumentBuilder();
			doc = docConstru.parse(fchXml);
			doc.getDocumentElement().normalize();

			lstNoeux = doc.getElementsByTagName("Classe");
			mapRect = new HashMap<String, Rectangle>();

			for (int i = 0; i < lstNoeux.getLength(); i++)
			{
				GereXml.extrairePositionClasse(mapRect, lstNoeux, i);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement des positions XML : " + e.getMessage());
		}	
		return mapRect;
	}

	private static void extrairePositionClasse(HashMap<String, Rectangle> mapRect, NodeList lstNoeux, int i)
	{
		Element elm;
		String nom;

		String x;
		String y;
		String w;
		String h;

		Node node = lstNoeux.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			elm = (Element) node;
			nom = elm.getAttribute("nom");

			x = elm.getAttribute("x");
			y = elm.getAttribute("y");
			w = elm.getAttribute("largeur");
			h = elm.getAttribute("hauteur");

			if (!x.isEmpty() && !y.isEmpty() && !w.isEmpty() && !h.isEmpty())
			{
				try 
				{
					mapRect.put(nom, new Rectangle(Integer.parseInt(x), 
					                               Integer.parseInt(y),
												   Integer.parseInt(w), 
												   Integer.parseInt(h)));
				} 
				catch (NumberFormatException ex) { }
			}
		}
	}

	public static ArrayList<Liaison> chargerLiaisonsXml(String chemin, ArrayList<Classe> classes)
	{
		try
		{
			File fchXml = new File(chemin);
			DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docConstru = docFact.newDocumentBuilder();
			Document doc = docConstru.parse(fchXml);
			doc.getDocumentElement().normalize();

			ArrayList<Liaison> liaisons = new ArrayList<>();

			NodeList lstNoeux = doc.getElementsByTagName("Liaison");

			for (int i = 0; i < lstNoeux.getLength(); i++)
			{
				Node node = lstNoeux.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elm = (Element) node;
					String from = elm.getAttribute("from");
					String to = elm.getAttribute("to");
					String nomVar = elm.getAttribute("nomVar");

					String fmin = elm.getAttribute("fromMin");
					String fmax = elm.getAttribute("fromMax");
					String tmin = elm.getAttribute("toMin");
					String tmax = elm.getAttribute("toMax");

					Classe cFrom = null; Classe cTo = null;
					for (Classe c : classes)
					{
						if (c.getNom().equals(from)) cFrom = c;
						if (c.getNom().equals(to)) cTo = c;
					}

					if (cFrom != null && cTo != null)
					{
						Multiplicite mFrom = new Multiplicite(fmin == null ? "" : fmin, fmax == null ? "" : fmax);
						Multiplicite mTo   = new Multiplicite(tmin == null ? "" : tmin, tmax == null ? "" : tmax);

						Liaison l = new Liaison(cFrom, cTo, mFrom, mTo, nomVar, null);
						liaisons.add(l);
					}
				}
			}

			return liaisons;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement des liaisons XML : " + e.getMessage());
		}
	}
}

