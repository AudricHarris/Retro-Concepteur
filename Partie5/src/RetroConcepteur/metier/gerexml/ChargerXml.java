package RetroConcepteur.metier.gerexml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.metier.classe.Liaison;
import RetroConcepteur.metier.classe.Multiplicite;
import RetroConcepteur.metier.classe.Parametre;
import RetroConcepteur.metier.classe.Position;

public class ChargerXml
{
	private static Document chargerUnXml(String chemin) throws Exception
	{
		File fchXml = new File(chemin);
		DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docConstru = docFact.newDocumentBuilder();
		Document doc = docConstru.parse(fchXml);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public static ArrayList<Classe> chargerClassesXml(String chemin)
	{
		Document doc;

		NodeList lstNoeux;
		ArrayList<Classe> classes;

		try
		{
			doc = chargerUnXml(chemin);

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
		boolean addOnly;

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
				addOnly = Boolean.parseBoolean(aEl.getAttribute("addOnly"));

				c.ajouterAttribut(nom, constante, type, vis, statique, addOnly);
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
	/// Chargement des positions et liaisons /////////
	//////////////////////////////////////////////////


	public static HashMap<String, Position> chargerPositionsXml(String chemin)	
	{
		Document doc;

		NodeList lstNoeux;
		HashMap<String, Position> mapPos;

		try
		{
			doc = chargerUnXml(chemin);

			lstNoeux = doc.getElementsByTagName("Classe");
			mapPos = new HashMap<String, Position>();

			for (int i = 0; i < lstNoeux.getLength(); i++)
			{
				ChargerXml.extrairePositionClasse(mapPos, lstNoeux, i);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement des positions XML : " + e.getMessage());
		}	
		return mapPos;
	}

	private static void extrairePositionClasse(HashMap<String, Position> mapPos, NodeList lstNoeux, int i)
	{
		Element elm;
		String nom;

		String x;
		String y;
		String largeur;
		String hauteur;

		Node node = lstNoeux.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			elm = (Element) node;
			nom = elm.getAttribute("nom");

			x = elm.getAttribute("x");
			y = elm.getAttribute("y");
			largeur = elm.getAttribute("largeur");
			hauteur = elm.getAttribute("hauteur");

			if (!x.isEmpty() && !y.isEmpty() && !largeur.isEmpty() && !hauteur.isEmpty())
			{
				try 
				{
					mapPos.put( nom,
								new Position(
									Integer.parseInt(x),
									Integer.parseInt(y),
									Integer.parseInt(largeur),
									Integer.parseInt(hauteur)
								)
							);
				} 
				catch (NumberFormatException ex) { }
			}
		}
	}

	public static ArrayList<Liaison> chargerLiaisonsXml(String chemin, ArrayList<Classe> classes) 
	{
		Element elm;
		Liaison l;
		Node noeux;
		try
		{
			Document doc = chargerUnXml(chemin);
			ArrayList<Liaison> liaisons = new ArrayList<>();
			NodeList lstNoeux = doc.getElementsByTagName("Liaison");

			for (int i = 0; i < lstNoeux.getLength(); i++)
			{
				noeux = lstNoeux.item(i);
				if (noeux.getNodeType() == Node.ELEMENT_NODE)
				{
					elm = (Element) noeux;
					l = creerLiaisonDepuisElement(elm, classes);
					if (l != null)
						liaisons.add(l);
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

	private static Liaison creerLiaisonDepuisElement(Element elm, ArrayList<Classe> classes)
	{
		Multiplicite mltde;
		Multiplicite mltVers;

		String de = elm.getAttribute("de");
		String vers = elm.getAttribute("vers");
		String nomVar = elm.getAttribute("nomVar");

		Classe clsDe = trouverClasse(classes, de);
		Classe clsVers = trouverClasse(classes, vers);

		if (clsDe == null || clsVers == null) return null;

		mltde   = new Multiplicite( elm.getAttribute("deMin"),
								    elm.getAttribute("deMax")
								  );
		mltVers = new Multiplicite( elm.getAttribute("versMin"),
									elm.getAttribute("versMax")
								  );
		return new Liaison(clsDe, clsVers, mltde, mltVers, nomVar, null);
	}

	private static Classe trouverClasse(ArrayList<Classe> classes, String nom)
	{
		for (Classe c : classes)
		{
			if (c.getNom().equals(nom))
				return c;
		}
		return null;
	}
}