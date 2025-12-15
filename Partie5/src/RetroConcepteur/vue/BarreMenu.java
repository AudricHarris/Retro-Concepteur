package RetroConcepteur.vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;



public class BarreMenu extends JMenuBar implements ActionListener
{
	/*-----------*/
	/* Attributs */
	/*-----------*/

	private FrameUML   frame;

	private String[][] modeleBarre = {  { "M", "Fichier",               "F"                    },
										{ "I", "Ouvrir",                "O", "control O"       },
										{ "I", "Sauvegarder",           "S", "control S"       },
										{ "I", "Exporter",              "X", "control X"       },
										{ "S"                                                  },
										{ "I", "Quitter",               "Q", "alt F4"          },
										{ "M", "Edition",               "E"                    }};	

	/*--------------*/
	/* Constructeur */
	/*--------------*/

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

					if (this.modeleBarre[i].length > 2) {
						item.setMnemonic(this.modeleBarre[i][2].charAt(0));
					}

					if (this.modeleBarre[i].length == 4) {
						item.setAccelerator(KeyStroke.getKeyStroke(this.modeleBarre[i][3]));
					}

					item.addActionListener(this);
					menuCourant.add(item);
				}

				case "S" -> { menuCourant.addSeparator(); }
			}
		}
	

		this.setVisible( true );
	}

	/*-----------------*/
	/* Autres méthodes */
	/*-----------------*/

	public void actionPerformed ( ActionEvent e )
	{
		String cmd = e.getActionCommand();

		switch (cmd) 
		{
			case "Ouvrir"      -> this.frame.ouvrirFichier();
			case "Sauvegarder" -> this.frame.sauverFichier();
			case "Exporter" -> this.frame.exporter();
			case "Quitter"     -> System.exit(0);

			default        -> System.out.println("Aucune action associée à : " + cmd);
		}
	}
}
