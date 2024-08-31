import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AjouterTable extends JDialog{
    private JTextField nomTable;
    private JTextField nombreColone;
    private JPanel Addtable;
    private JButton continuerButton;
    private JButton annulerButton;
    public AjouterTable(JFrame parent)
    {
        super(parent);
        setTitle("Ajouter une table");
        setContentPane(Addtable);
        setMinimumSize(new Dimension(400,200));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(annulerButton).dispose();
            }
        });

        continuerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nbColoneS = nombreColone.getText().trim();
                String nom_table = nomTable.getText().trim();
                // Vérification de la nullité ou de la chaîne vide
                if (nbColoneS == null || nbColoneS.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vous devez entrer un nombre strictement positif.");
                    return; // Quitter la méthode car aucune conversion n'est nécessaire
                } else if (nom_table.trim().isEmpty() || Character.isDigit(nom_table.charAt(0))) {
                    JOptionPane.showMessageDialog(null, "Vous devez entrer un nom de la table valide.");
                    return;
                }

                int nbColone;
                try {
                    nbColone = Integer.parseInt(nbColoneS);

                    if (nbColone <= 0) {
                        JOptionPane.showMessageDialog(null, "Vous devez entrer un nombre strictement positif.");
                    }
                    else
                    {
                        JFrame f = new JFrame();
                        AddColoneTable colone = new AddColoneTable(f,nbColone,nom_table);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Merci d'entrer un nombre strectemnt positif.");
                }
                SwingUtilities.getWindowAncestor(continuerButton).dispose();
            }
        });
        setVisible(true);

    }

}
