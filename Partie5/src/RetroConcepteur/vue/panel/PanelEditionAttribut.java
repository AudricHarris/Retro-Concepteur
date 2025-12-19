package RetroConcepteur.vue.panel;

import RetroConcepteur.Controleur;
import RetroConcepteur.metier.classe.Classe;
import RetroConcepteur.vue.FrameEdition;
import RetroConcepteur.metier.classe.Attribut;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class PanelEditionAttribut extends JPanel implements ActionListener
{
    Controleur ctrl;
    FrameEdition frmParent;
    Classe classe;
    JButton btnValider;
    JCheckBox cbFrozen;
    JCheckBox cbAddOnly;
    ArrayList<JCheckBox> listeCbFrozen;
    ArrayList<JCheckBox> listeCbAddOnly;
    ArrayList<JTextField> listeTxtNom;
    public PanelEditionAttribut(Controleur ctrl, FrameEdition frmParent, Classe classe) 
    {
        this.ctrl = ctrl;
        this.classe = classe;
        this.frmParent = frmParent;
        this.listeCbFrozen = new ArrayList<JCheckBox>();
        this.listeCbAddOnly = new ArrayList<JCheckBox>();
        this.listeTxtNom = new ArrayList<JTextField>();
        this.setLayout(new BorderLayout());
        
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelGrille = new JPanel(new GridLayout(0, 3, 5, 0));
        
        
        panelGrille.add(creerLabelTitre("Nom", true));
        panelGrille.add(creerLabelTitre("{freeze}", true));
        panelGrille.add(creerLabelTitre("{addOnly}", true));

        for (Attribut att : classe.getLstAttribut()) 
        {

            JTextField txtNom = new JTextField(" " + att.getNom());
			
            txtNom.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            panelGrille.add(txtNom);
            this.listeTxtNom.add(txtNom);

            // Colonne 2
            cbFrozen = new JCheckBox();
            cbFrozen.setHorizontalAlignment(SwingConstants.CENTER);
            cbFrozen.setSelected(att.isConstante());
            cbFrozen.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Ligne séparatrice
            cbFrozen.setBorderPainted(true);
            panelGrille.add(cbFrozen);
            this.listeCbFrozen.add(cbFrozen);

            // Colonne 3
            cbAddOnly = new JCheckBox();
            cbAddOnly.setHorizontalAlignment(SwingConstants.CENTER);
            cbAddOnly.setSelected(att.isAddOnly());
            cbAddOnly.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Ligne séparatrice
            cbAddOnly.setBorderPainted(true);
            panelGrille.add(cbAddOnly);
            this.listeCbAddOnly.add(cbAddOnly);
        }

        JPanel panelConteneur = new JPanel(new BorderLayout());
        panelConteneur.add(panelGrille, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelConteneur);
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        this.add(scroll, BorderLayout.CENTER);

        btnValider = new JButton("Valider");
        btnValider.addActionListener(this);
        this.add(btnValider, BorderLayout.SOUTH);
    }

    private JLabel creerLabelTitre(String texte, boolean gris) 
    {
        JLabel lbl = new JLabel(texte, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        if (gris) {
            lbl.setOpaque(true);
            lbl.setBackground(new Color(220, 220, 220)); 
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)); 
        }
        return lbl;
    }
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) 
    {
        if (e.getSource() == this.btnValider) 
        {
            for (int i = 0; i < this.listeCbFrozen.size(); i++) 
            {
                JCheckBox cbFrozen = this.listeCbFrozen.get(i);
                JCheckBox cbAddOnly = this.listeCbAddOnly.get(i);
                String nom = this.listeTxtNom.get(i).getText().trim();
                Attribut att = this.classe.getLstAttribut().get(i);

                this.ctrl.majAttribut(att, nom, cbFrozen.isSelected(), cbAddOnly.isSelected());
            }
            this.ctrl.majIHM();
            this.frmParent.dispose();
        }
    }
}