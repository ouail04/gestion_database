import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
//import java.util.ArrayList;

public class AddColoneTable extends JDialog {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    private JTable table1;
    private JButton terminerButton;
    private JButton annulerButton;
    private JScrollPane ColoneTable;
    private DefaultTableModel tableModel;

    public AddColoneTable(JFrame frame, int ligne,String nomTable) {
        super(frame);
        setTitle("Ajouter une table");

        tableModel = new DefaultTableModel();
        tableModel.addColumn("nom colone");
        tableModel.addColumn("type de donnée");
        tableModel.addColumn("contrainte");
        tableModel.addColumn("conteneur");
        tableModel.addColumn("table reference");
        tableModel.addColumn("champs reference");

        table1 = new JTable(tableModel);
        ColoneTable = new JScrollPane(table1);

        terminerButton = new JButton("Terminer");
        annulerButton = new JButton("Annuler");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(ColoneTable, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(terminerButton);
        buttonPanel.add(annulerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(panel);
        addRowsToTable(ligne);

        setMinimumSize(new Dimension(500, 400));
        setModal(true);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        terminerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
                int ligne = tableModel.getRowCount();
                int taille = ligne;
                for (int i = 0; i < ligne; i++) {
                    if (tableModel.getValueAt(i, 3)== "FORIEGN KEY") {
                        taille++;
                    }
                }
                String[] sql_ligne = new String[taille];
                String nomC = "";
                String typeC = "";
                String container = "";
                String conteneur = "";
                for (int i = 0 ; i < sql_ligne.length ; i++)
                {
                    nomC = (String)tableModel.getValueAt(i,0);
                    typeC = (String)tableModel.getValueAt(i,1);
                    container = (String)tableModel.getValueAt(i,2);
                    conteneur  = (String)tableModel.getValueAt(i,3);

                    if (typeC == "Chaine de caractère" )
                    {
                        typeC = "VARCHAR(255)";
                    } else if (typeC == "Entier") {
                        typeC = "INT";
                    } else if (typeC == "Reel"){
                        typeC = "DOUBLE";
                    } else if (typeC == "Texte") {
                        typeC = "TEXT";
                    } else if (typeC == "Heure") {
                        typeC = "TIME";
                    }

                    if (container == "AUCUN")
                        container = "";
                    if (conteneur == "AUCUN")
                        conteneur = "";
                    if (conteneur == "FORIEGN KEY")
                    {
                        sql_ligne[i] = nomC+" "+typeC+" "+container;
                        String ref_table =(String)tableModel.getValueAt(i,4);
                        String ref_attribut = (String)tableModel.getValueAt(i,5);
                        i++;
                        sql_ligne[i] = "FOREIGN KEY ("+nomC+") REFERENCES "+ref_table+"("+ref_attribut+")";
                    } else
                    {
                        sql_ligne[i] = nomC+" "+typeC+" "+conteneur+" "+container;
                    }
                }
                String requette = "CREATE TABLE "+nomTable+" ( " ;
                for (int i = 0 ; i < sql_ligne.length ; i++)
                {
                    if (i == sql_ligne.length-1)
                    {
                        requette += sql_ligne[i] ;
                    }
                    else
                    {
                        requette += sql_ligne[i]+",";
                    }
                }
                requette += ")" ;
                JOptionPane.showMessageDialog(null,requette);

                try {
                    Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(requette);
                    statement.close();
                    connection.close();
                    JOptionPane.showMessageDialog(null,"La table "+nomTable+" été criee avec succes");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"erreur à la creation de la table :"+ex.getMessage());
                }
                SwingUtilities.getWindowAncestor(terminerButton).dispose();
            }

        });

        setVisible(true);
    }

    private void addRowsToTable(int numberOfLines) {
        for (int i = 0; i < numberOfLines; i++) {
            String columnName = JOptionPane.showInputDialog("Entrez le nom de la colonne "+(i+1)+":");
            // Demander à l'utilisateur de saisir le nom de la colonne
            while (columnName.trim().isEmpty() || Character.isDigit(columnName.charAt(0))) {
                columnName = JOptionPane.showInputDialog("Merci d'entrer un nom valide pour la colonne " + (i + 1) + ":");
            }

            String[] type = {"Chaine de caractère","Texte","Entier","Reel","Date","Heure"};
            String[] container = {"AUCUN","UNIQUE","NOT NULL"};
            String[] conteneur = {"AUCUN","FORIEGN KEY","PRIMARY KEY"};
            String type_name = showDialog(type,"selection le type de la colone "+(i+1)+":");
            String container_name = showDialog(container,"Selection Contrainte de la colone "+(i+1)+":");
            String conteneur_name = showDialog(conteneur,"selection le conteneur de la colone "+(i+1)+":");
            String ref_table = "NULL";
            String ref_champ = "NULL";
            if (conteneur_name == "FORIEGN KEY")
            {
                ref_table = showDialog(retrieveTableNames(),"selection de la table reference de votre FOREIGN KEY:");
                ref_champ = showDialog(retrieveColoneNames(ref_table),"selection de attribut reference de votre FOREIGN KEY:");
            }
            // Créer un tableau d'Object avec les données de la nouvelle ligne
            Object[] rowData = {columnName, type_name, container_name, conteneur_name,ref_table,ref_champ};

            // Ajouter la nouvelle ligne à la table
            tableModel.addRow(rowData);
        }
    }

    private String showDialog(String[] str,String nomLabel)
    {
        // Créer une liste d'éléments pour le JComboBox

        // Créer un JPanel contenant le JComboBox
        JPanel panel = new JPanel();
        JLabel label = new JLabel(nomLabel);
        JComboBox<String> comboBox = new JComboBox<>(str);

        panel.add(label);
        panel.add(comboBox);

        // Afficher le JOptionPane avec le JComboBox
        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Sélectionnez une option",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Traiter la sélection
        if (result == JOptionPane.OK_OPTION) {
            // Récupérer la valeur sélectionnée
            String selectedOption = (String) comboBox.getSelectedItem();
            return selectedOption;
        } else {
            return null;
        }

    }

    public static String[] retrieveTableNames() {
        List tableNames = new List();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT table_name FROM user_tables");

            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                tableNames.add(tableName);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"erreur lors de la recuperation des tables :"+e.getMessage());
        }

        // Convertir la liste en tableau de chaînes
        String[] resultArray = new String[tableNames.getItemCount()];
        for (int i = 0; i < tableNames.getItemCount(); i++) {
            resultArray[i] = tableNames.getItem(i);
        }
        return resultArray;
    }
    public static String[] retrieveColoneNames(String tableName) {
        List ColonesNames = new List();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                ColonesNames.add(columnName);
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"erreur lors de la recuperation des nom des colone :"+e.getMessage());
        }

        // Convertir la liste en tableau de chaînes
        String[] resultArray = new String[ColonesNames.getItemCount()];
        for (int i = 0; i < ColonesNames.getItemCount(); i++) {
            resultArray[i] = ColonesNames.getItem(i);
        }
        return resultArray;
    }
    
}

