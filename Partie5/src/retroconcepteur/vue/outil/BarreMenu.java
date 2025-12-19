package retroconcepteur.vue.outil;

import retroconcepteur.vue.FrameUML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Classe responsable de la creation de la barre de menu de l'application.
 * * @author [Keryann Le Besque, Laurent Descourtis, Audric Harris, Pol Armand Bermendora, Lucas Leprevost] 
 * @version 2.0
 */

public class BarreMenu extends JMenuBar implements ActionListener
{
	/*---------------------------------*/
	/*           Attributs             */
	/*---------------------------------*/

	private FrameUML   frame;
	private JCheckBoxMenuItem cbAfficher;

	private String[][] modeleBarre = {  { "M", "Fichier",                      "F"                    },
										{ "I", "Ouvrir Dossier",               "O", "control O"       },
										{ "I", "Ouvrir Xml",                   "X", "control X"       },
										{ "I", "Exporter en Image",            "I", "control I"       },
										{ "I", "Sauvegarder en xml",           "S", "control S"       },
										{ "S"                                                         },
										{ "I", "Quitter",                      "Q", "alt F4"          },
										{ "M", "Edition",                      "E"                    },
										{ "C", "Afficher implements/inteface", "A", "control A"       }};

	/*---------------------------------------*/
	/*            Constructeur               */
	/*---------------------------------------*/

	public BarreMenu (FrameUML frame)
	{
		this.frame = frame;

		JMenu menuCourant = null;
		for (int i = 0; i < this.modeleBarre.length; i++)
		{
			String s = this.modeleBarre[i][0];

			switch (s)
			{
				case "M" -> 
				{
					menuCourant = new JMenu(this.modeleBarre[i][1]);
					menuCourant.setMnemonic(this.modeleBarre[i][2].charAt(0));
					this.add(menuCourant);
				}
				
				case "I" -> 
				{
					JMenuItem item = new JMenuItem(this.modeleBarre[i][1]);

					if (this.modeleBarre[i].length > 2) 
						item.setMnemonic(this.modeleBarre[i][2].charAt(0));
					

					if (this.modeleBarre[i].length == 4) 
						item.setAccelerator(KeyStroke.getKeyStroke(this.modeleBarre[i][3]));
					

					item.addActionListener(this);
					menuCourant.add(item);
				}

				case "C" -> 
				{
					JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(this.modeleBarre[i][1]);

					if (this.modeleBarre[i].length > 2)
						checkBox.setMnemonic(this.modeleBarre[i][2].charAt(0));

					if (this.modeleBarre[i].length == 4)
						checkBox.setAccelerator(KeyStroke.getKeyStroke(this.modeleBarre[i][3]));

					checkBox.addActionListener(this);
					menuCourant.add(checkBox);

					if ("Afficher implements/inteface".equals(this.modeleBarre[i][1])) 
					{
						this.cbAfficher = checkBox;
						this.cbAfficher.setSelected(true);
					}
				}

				case "S" -> { menuCourant.addSeparator(); }
			}
		}
	

		this.setVisible( true );
	}

	/*-------------------------------------*/
	/*           Autres methodes           */
	/*-------------------------------------*/

	public void actionPerformed ( ActionEvent e )
	{
		String cmd = e.getActionCommand();

		switch (cmd) 
		{
			case "Ouvrir Dossier"      			-> this.frame.ouvrirFichier();
			case "Ouvrir Xml"  					-> this.frame.ouvrirXml();
			case "Sauvegarder en xml"           -> this.frame.sauvegardeFichier();
			case "Exporter en Image"    		-> this.frame.exporterImage();			
			case "Afficher implements/inteface" -> this.frame.afficherImplHerit(this.cbAfficher.getState());
			case "Quitter"     					-> System.exit(0);

			default        -> System.out.println("Aucune action associee a : " + cmd);
		}
	}
}
