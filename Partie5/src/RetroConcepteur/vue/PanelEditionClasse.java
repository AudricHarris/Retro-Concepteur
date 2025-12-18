package RetroConcepteur.vue;

import RetroConcepteur.Controller;
import RetroConcepteur.metier.classe.Classe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PanelEditionClasse extends JPanel implements ActionListener
{
    private Controller ctrl;
    private FrameEdition frmParent; // On garde si besoin, sinon on ferme via SwingUtilities
    private Classe classe; // La classe à modifier

    // Composants graphiques
    private JTextField txtNom;
    private JCheckBox cbAbstract;
    private JCheckBox cbInterface;
    private JButton btnValider;
    private JButton btnAnnuler;

    public PanelEditionClasse(Controller ctrl, FrameEdition frmParent, Classe classe)
    {
        this.ctrl = ctrl;
        this.frmParent = frmParent;
        this.classe = classe;

        // 1. Configuration du panel
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 2. Titre en haut
        JLabel lblTitre = new JLabel("Édition de la classe : " + classe.getNom());
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitre.setHorizontalAlignment(JLabel.CENTER);
        this.add(lblTitre, BorderLayout.NORTH);

        // 3. Formulaire au centre (GridLayout simple : 3 lignes, 2 colonnes)
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Champ Nom
        panelForm.add(new JLabel("Nom de la classe :"));
        this.txtNom = new JTextField(classe.getNom());
        panelForm.add(this.txtNom);

        // Checkbox Abstract
        panelForm.add(new JLabel("Abstrait :"));
        this.cbAbstract = new JCheckBox();
        this.cbAbstract.setSelected(classe.isAbstract());
        panelForm.add(this.cbAbstract);

        // Checkbox Interface
        panelForm.add(new JLabel("Interface :"));
        this.cbInterface = new JCheckBox();
        this.cbInterface.setSelected(classe.isInterface());
        
        if(classe.isInterface()){
            cbAbstract.setSelected(true);
            cbAbstract.setEnabled(false);
        }

        panelForm.add(this.cbInterface);

        this.add(panelForm, BorderLayout.CENTER);

		
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        this.btnAnnuler = new JButton("Annuler");
        this.btnAnnuler.addActionListener(this);
        
        this.btnValider = new JButton("Valider");
        this.btnValider.addActionListener(this);
        this.btnValider.setBackground(new Color(220, 255, 220)); 

        panelBoutons.add(this.btnAnnuler);
        panelBoutons.add(this.btnValider);
        
        this.add(panelBoutons, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnValider)
        {
            // --- 1. Appliquer les changements à l'objet Classe ---
            
            // Modifier le nom (Attention : il faudrait vérifier s'il est unique dans le Controller idéalement)
            String nouveauNom = this.txtNom.getText().trim();
            if (!nouveauNom.isEmpty()) 
			{
				this.ctrl.majClasse(this.classe, nouveauNom, this.classe.isAbstract(), this.classe.isInterface());
            }

            this.classe.setIsAbstract(this.cbAbstract.isSelected());
            this.classe.setIsInterface(this.cbInterface.isSelected());

            this.ctrl.majIHM(); 
            fermerFenetre();
        }
        else if (e.getSource() == this.btnAnnuler)
        {
            fermerFenetre();
        }
    }

    private void fermerFenetre()
    {
        if (this.frmParent != null) 
		{
            this.frmParent.dispose();
        } 
		else 
		{
			
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }
}