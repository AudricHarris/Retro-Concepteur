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
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Diagramme");
			doc.appendChild(rootElement);

			for (Classe c : lstClasses)
			{
				Element classEl = doc.createElement("Classe");
				classEl.setAttribute("nom", c.getNom());
				classEl.setAttribute("isAbstract", Boolean.toString(c.isAbstract()));
				classEl.setAttribute("isInterface", Boolean.toString(c.isInterface()));
				if (c.getNomHeritageClasse() != null)
					classEl.setAttribute("extends", c.getNomHeritageClasse());
				// gere interfaces
				if (c.getLstInterfaces() != null && !c.getLstInterfaces().isEmpty())
				{
					Element interfacesEl = doc.createElement("Interfaces");
					for (String inter : c.getLstInterfaces())
					{
						Element iEl = doc.createElement("Interface");
						iEl.setAttribute("nom", inter);
						interfacesEl.appendChild(iEl);
					}
					classEl.appendChild(interfacesEl);
				}

				Rectangle r = mapRect.get(c);
				if (r != null) 
				{
					classEl.setAttribute("x", Integer.toString(r.getX()));
					classEl.setAttribute("y", Integer.toString(r.getY()));
					classEl.setAttribute("width", Integer.toString(r.getTailleX()));
					classEl.setAttribute("height", Integer.toString(r.getTailleY()));
				}

				// gere Attributs
				Element attsEl = doc.createElement("Attributs");
				for (Attribut a : c.getLstAttribut()) 
				{
					Element attEl = doc.createElement("Attribut");
					attEl.setAttribute("nom", a.getNom());
					attEl.setAttribute("type", a.getType());
					attEl.setAttribute("visibilite", a.getVisibilite() == null ? "" : a.getVisibilite());
					attEl.setAttribute("constante", Boolean.toString(a.isConstante()));
					attEl.setAttribute("static", Boolean.toString(a.isStatic()));
					attsEl.appendChild(attEl);
				}
				classEl.appendChild(attsEl);

				// gere Methodes
				Element methsEl = doc.createElement("Methodes");
				for (Methode m : c.getLstMethode()) {
					Element mEl = doc.createElement("Methode");
					mEl.setAttribute("nom", m.getNom());
					mEl.setAttribute("type", m.getType());
					mEl.setAttribute("visibilite", m.getVisibilite() == null ? "" : m.getVisibilite());
					mEl.setAttribute("static", Boolean.toString(m.isStatic()));

					// gere Parametres
					Element paramsEl = doc.createElement("Parametres");
					for (Parametre p : m.getLstParam()) 
					{
						Element pEl = doc.createElement("Parametre");
						pEl.setAttribute("nom", p.getNom());
						pEl.setAttribute("type", p.getType());
						paramsEl.appendChild(pEl);
					}
					mEl.appendChild(paramsEl);

					methsEl.appendChild(mEl);
				}
				classEl.appendChild(methsEl);


				rootElement.appendChild(classEl);
			}

				// Sauvegarde des liaisons si fournies
				if (lstLiaisons != null && !lstLiaisons.isEmpty())
				{
					Element liaisonsEl = doc.createElement("Liaisons");
					for (Liaison l : lstLiaisons)
					{
						Element lEl = doc.createElement("Liaison");
						lEl.setAttribute("from", l.getFromClass().getNom());
						lEl.setAttribute("to", l.getToClass().getNom());
						lEl.setAttribute("type", l.getType());
						lEl.setAttribute("nomVar", l.getNomVar() == null ? "" : l.getNomVar());
						// multiplicites
						if (l.getFromMultiplicity() != null)
						{
							lEl.setAttribute("fromMin", l.getFromMultiplicity().getBorneInf());
							lEl.setAttribute("fromMax", l.getFromMultiplicity().getBorneSup());
						}
						if (l.getToMultiplicity() != null)
						{
							lEl.setAttribute("toMin", l.getToMultiplicity().getBorneInf());
							lEl.setAttribute("toMax", l.getToMultiplicity().getBorneSup());
						}
						liaisonsEl.appendChild(lEl);
					}
					rootElement.appendChild(liaisonsEl);
				}

			// ecrit dans doc xml
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(chemin));

			transformer.transform(source, result);

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors de la sauvegarde XML : " + e.getMessage());
		}
	}

	public static ArrayList<Classe> chargerClassesXml(String chemin)
	{
		try
		{
			File fXmlFile = new File(chemin);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Classe");
			ArrayList<Classe> classes = new ArrayList<Classe>();

			for (int i = 0; i < nList.getLength(); i++)
			{
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) node;
					String nom = eElement.getAttribute("nom");
					Classe c = new Classe(nom);

					String sAbstract = eElement.getAttribute("isAbstract");
					if (sAbstract != null && !sAbstract.isEmpty()) c.setIsAbstract(Boolean.parseBoolean(sAbstract));
					String sInterface = eElement.getAttribute("isInterface");
					if (sInterface != null && !sInterface.isEmpty()) c.setIsInterface(Boolean.parseBoolean(sInterface));
					String sExtends = eElement.getAttribute("extends");
					if (sExtends != null && !sExtends.isEmpty()) c.setNomHeritageClasse(sExtends);

					NodeList interfacesNodes = eElement.getElementsByTagName("Interface");
					for (int ii = 0; ii < interfacesNodes.getLength(); ii++) 
					{
						Node in = interfacesNodes.item(ii);
						if (in.getNodeType() == Node.ELEMENT_NODE) 
						{
							Element iEl = (Element) in;
							String iname = iEl.getAttribute("nom");
							if (iname != null && !iname.isEmpty()) c.ajouterInterface(iname);
						}
					}

					NodeList atts = eElement.getElementsByTagName("Attribut");
					for (int j = 0; j < atts.getLength(); j++)
					{
						Node attNode = atts.item(j);
						if (attNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element aEl = (Element) attNode;
							String anom = aEl.getAttribute("nom");
							String atype = aEl.getAttribute("type");
							String avis = aEl.getAttribute("visibilite");
							boolean aconst = Boolean.parseBoolean(aEl.getAttribute("constante"));
							boolean astatic = Boolean.parseBoolean(aEl.getAttribute("static"));
							c.ajouterAttribut(anom, aconst, atype, avis, astatic);
						}
					}

					NodeList meths = eElement.getElementsByTagName("Methode");
					for (int j = 0; j < meths.getLength(); j++)
					{
						Node mNode = meths.item(j);
						if (mNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element mEl = (Element) mNode;
							String mnom = mEl.getAttribute("nom");
							String mtype = mEl.getAttribute("type");
							String mvis = mEl.getAttribute("visibilite");
							boolean mstatic = Boolean.parseBoolean(mEl.getAttribute("static"));

							ArrayList<Parametre> params = new ArrayList<>();
							NodeList paramsList = mEl.getElementsByTagName("Parametre");
							for (int k = 0; k < paramsList.getLength(); k++)
							{
								Node pNode = paramsList.item(k);
								if (pNode.getNodeType() == Node.ELEMENT_NODE)
								{
									Element pEl = (Element) pNode;
									String pnom = pEl.getAttribute("nom");
									String ptype = pEl.getAttribute("type");
									params.add(new Parametre(pnom, ptype));
								}
							}

							c.ajouterMethode(mvis, mnom, mtype, params, mstatic);
						}
					}

					classes.add(c);
				}
			}

			return classes;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement XML : " + e.getMessage());
		}
	}

	public static HashMap<String, Rectangle> chargerPositionsXml(String chemin)
	{
		try
		{
			File fXmlFile = new File(chemin);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Classe");
			HashMap<String, Rectangle> mapRect = new HashMap<String, Rectangle>();

			for (int i = 0; i < nList.getLength(); i++)
			{
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) node;
					String nom = eElement.getAttribute("nom");

					String sx = eElement.getAttribute("x");
					String sy = eElement.getAttribute("y");
					String sw = eElement.getAttribute("width");
					String sh = eElement.getAttribute("height");
					if (!sx.isEmpty() && !sy.isEmpty() && !sw.isEmpty() && !sh.isEmpty())
					{
						try 
						{
							int x = Integer.parseInt(sx);
							int y = Integer.parseInt(sy);
							int w = Integer.parseInt(sw);
							int h = Integer.parseInt(sh);
							Rectangle r = new Rectangle(x, y, w, h);
							mapRect.put(nom, r);
						} 
						catch (NumberFormatException ex) { }
					}
				}
			}

			return mapRect;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors du chargement des positions XML : " + e.getMessage());
		}
	}

	public static ArrayList<Liaison> chargerLiaisonsXml(String chemin, ArrayList<Classe> classes)
	{
		try
		{
			File fXmlFile = new File(chemin);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			ArrayList<Liaison> liaisons = new ArrayList<>();

			NodeList nList = doc.getElementsByTagName("Liaison");

			for (int i = 0; i < nList.getLength(); i++)
			{
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) node;
					String from = eElement.getAttribute("from");
					String to = eElement.getAttribute("to");
					String nomVar = eElement.getAttribute("nomVar");

					String fmin = eElement.getAttribute("fromMin");
					String fmax = eElement.getAttribute("fromMax");
					String tmin = eElement.getAttribute("toMin");
					String tmax = eElement.getAttribute("toMax");

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

