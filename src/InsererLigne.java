import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InsererLigne extends JDialog {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";
    private JPanel insererPanel2;
    private JPanel insererPanel1;
    private JButton terminerButton;
    private JButton annulerButton;
    private JLabel[] labels;
    private JTextField[] textFields;

    public InsererLigne(JFrame parent, String tableName) {
        super(parent);
        setTitle("Update une ligne");
        setContentPane(insererPanel1);
        setMinimumSize(new Dimension(500, 550));
        setModal(true);
        setLocationRelativeTo(parent);
        String[] coloneNames = AddColoneTable.retrieveColoneNames(tableName);
        labels = new JLabel[coloneNames.length];
        textFields = new JTextField[coloneNames.length];

        insererPanel2.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Marge entre les composants

        for (int i = 0; i < coloneNames.length; i++) {
            labels[i] = new JLabel(coloneNames[i]);
            textFields[i] = new JTextField();

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.WEST; // Alignement à gauche
            insererPanel2.add(labels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            insererPanel2.add(textFields[i], gbc);
        }

        terminerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String requete = "INSERT INTO "+tableName+" VALUES (";
                for (int i = 0 ; i < textFields.length ;i++)
                {
                    if (textFields[i].getText().trim().isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"les champs ne peuvent pas être vides.");
                    }
                    else
                    {
                        if (UpdateTable.isNumeric(textFields[i].getText()))
                        {
                            requete += textFields[i].getText();
                        }
                        else
                        {
                            requete += "'"+textFields[i].getText()+"'";
                        }
                        if (i != textFields.length-1)
                        {
                            requete += ", " ;
                        }
                        else
                        {
                            requete += " )" ;
                        }
                    }

                }
                try
                {
                    Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(requete);
                    statement.close();
                    connection.close();
                    JOptionPane.showMessageDialog(null,"l'insertion est succes ! ");
                    dispose();
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null,"erreur dans l'insertion de champs "+ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}
