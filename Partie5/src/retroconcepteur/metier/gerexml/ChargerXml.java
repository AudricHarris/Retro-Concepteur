package retroconcepteur.metier.gerexml;

// Nos paquetage
import retroconcepteur.metier.classe.Classe;
import retroconcepteur.metier.classe.Liaison;
import retroconcepteur.metier.classe.Multiplicite;
import retroconcepteur.metier.classe.Parametre;
import retroconcepteur.metier.classe.Position;

// paquetage io
import java.io.File;

// paquetage util
import java.util.ArrayList;
import java.util.HashMap;

// paquetage xml
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// paquetage org
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe utilitaire permettant de charger un diagramme de classes
 * depuis un fichier XML.
 */
public class ChargerXml
{
	/*------------------------------------------*/
	/*       Methode de classe public           */
	/*------------------------------------------*/
	
	/**
	 * Charge les classes depuis un fichier XML.
	 *
	 * @param chemin chemin du fichier XML à lire
	 * @return liste des classes chargées
	 * @throws RuntimeException si une erreur survient lors du chargement
	 */
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
			throw new RuntimeException(e.getMessage());
		}
	}
		
	/**
	 * Charge les positions graphiques des classes depuis un fichier XML.
	 *
	 * @param chemin chemin du fichier XML à lire
	 * @return map associant le nom d'une classe à sa position graphique
	 * @throws RuntimeException si une erreur survient lors du chargement
	 */
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
			throw new RuntimeException("Erreur lors du chargement des positions XML : " + e.getMessage());
		}	
		return mapPos;
	}
		
	/**
	 * Charge les liaisons entre les classes depuis un fichier XML.
	 *
	 * @param chemin chemin du fichier XML à lire
	 * @param classes liste des classes déjà chargées
	 * @return liste des liaisons chargées
	 * @throws RuntimeException si une erreur survient lors du chargement
	 */
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
			throw new RuntimeException("Erreur lors du chargement des liaisons XML : " + e.getMessage());
		}
	}

	/*------------------------------------------*/
	/*      Methode de classe privee            */
	/*------------------------------------------*/

	/**
	 * Charge et parse un fichier XML.
	 *
	 * @param chemin chemin du fichier XML
	 * @return document XML normalisé
	 * @throws Exception en cas d'erreur de lecture ou de parsing
	 */
	private static Document chargerUnXml(String chemin) throws Exception
	{
		File fchXml = new File(chemin);
		DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docConstru = docFact.newDocumentBuilder();
		Document doc = docConstru.parse(fchXml);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Construit un objet {@link Classe} à partir d'un nœud XML.
	 *
	 * @param node nœud XML représentant une classe
	 * @return instance de {@link Classe} ou {@code null} si le nœud est invalide
	 */
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

	/**
	 * Lit les propriétés générales d'une classe (abstraite, interface, héritage).
	 *
	 * @param elm élément XML de la classe
	 * @param c classe à configurer
	 */
	private static void lireProprietesClasse(Element elm, Classe c)
	{
		String sAbstract;
		String sInterface;
		String sExtends;
		String sCachable;

		sAbstract = elm.getAttribute("isAbstract");
		if (!sAbstract.isEmpty())
			c.setIsAbstract(Boolean.parseBoolean(sAbstract));

		sInterface = elm.getAttribute("isInterface");
		if (!sInterface.isEmpty())
			c.setIsInterface(Boolean.parseBoolean(sInterface));

		sExtends = elm.getAttribute("extends");
		if (!sExtends.isEmpty())
			c.setNomHeritageClasse(sExtends);

		sCachable = elm.getAttribute("cachable");
		if (!sCachable.isEmpty())
			c.setCachable(Boolean.parseBoolean(sCachable));
	}

	/**
	 * Lit les interfaces implémentées par une classe.
	 *
	 * @param elm élément XML de la classe
	 * @param c classe à enrichir
	 */
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
	
	/**
	 * Lit les attributs d'une classe depuis le XML.
	 *
	 * @param elm élément XML de la classe
	 * @param c classe à enrichir
	 */
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

	/**
	 * Lit les méthodes d'une classe depuis le XML.
	 *
	 * @param elm élément XML de la classe
	 * @param c classe à enrichir
	 */
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

	/**
	 * Lit les paramètres d'une méthode.
	 *
	 * @param mEl élément XML représentant une méthode
	 * @return liste des paramètres de la méthode
	 */
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

	/**
	 * Extrait la position graphique d'une classe depuis le XML.
	 *
	 * @param mapPos map contenant les positions des classes
	 * @param lstNoeux liste des nœuds "Classe"
	 * @param i index du nœud à traiter
	 */
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

	/**
	 * Crée une liaison à partir d'un élément XML.
	 *
	 * @param elm élément XML représentant la liaison
	 * @param classes liste des classes existantes
	 * @return liaison construite ou {@code null} si invalide
	 */
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

	/**
	 * Recherche une classe par son nom dans une liste.
	 *
	 * @param classes liste des classes
	 * @param nom nom de la classe recherchée
	 * @return classe trouvée ou {@code null} si absente
	 */
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
