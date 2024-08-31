import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class EditerTable extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";
    private JTextField textField1;
    private JComboBox comboBox1;
    private JTable table2;
    private JButton chercherButton;
    private JButton updateButton;
    private JButton supprimerButton;
    private JPanel EditePanel;
    private JButton annulerButton;
    private JScrollPane scrolTable;
    private JButton ajouterLigneButton;

    public EditerTable(String tableName)
    {
        super();
        setTitle("Editer "+tableName);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //code start :
        insererColumnName(tableName);
        insererLignes(tableName);
        ajouterChois(tableName,comboBox1);


                    // les button :
        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> Main.createAndShowGUI());
            }
        });
        ajouterLigneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InsererLigne iinsererLigne = new InsererLigne(EditerTable.this , tableName);
                insererLignes(tableName);
            }
        });
        chercherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chercheValue = textField1.getText();
                String chercheType = (String) comboBox1.getSelectedItem();
                if (chercheType == "VOIR TOUS")
                {
                    insererLignes(tableName);
                }
                else {
                        insererLignes(tableName,chercheType,chercheValue);

                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateTable updateTable = new UpdateTable(EditerTable.this,tableName);
                insererLignes(tableName);
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table2.getSelectedRow();
                String[] columnNames = AddColoneTable.retrieveColoneNames(tableName);
                String[] columnValue = new String[columnNames.length];

                for (int i = 0; i < columnValue.length; i++) {
                    columnValue[i] = table2.getValueAt(selectedRow, i).toString();
                }

                // Construire la requête DELETE
                String requete = "DELETE FROM " + tableName + " WHERE ";

                for (int i = 0; i < columnNames.length; i++) {
                    requete += columnNames[i] + " = ?";

                    if (i < columnNames.length - 1) {
                        requete += " AND ";
                    }
                }

                try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                     PreparedStatement preparedStatement = connection.prepareStatement(requete)) {

                    // Définir les valeurs des paramètres dans la requête DELETE
                    for (int i = 0; i < columnNames.length; i++) {
                        preparedStatement.setString(i + 1, columnValue[i]);
                    }

                    // Exécuter la requête DELETE
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, rowsAffected + " ligne(s) supprimée(s) avec succès.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Aucune ligne supprimée. Vérifiez les valeurs de la condition.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors de la suppression : " + ex.getMessage());
                }
                insererLignes(tableName);
            }
        });

        // code end :
        setContentPane(EditePanel);
        setBounds(350,150,700,400);
        setVisible(true);


    }


    private static void ajouterChois(String tableName,JComboBox comboBox1)
    {
        String[] coloneName = AddColoneTable.retrieveColoneNames(tableName);
        comboBox1.addItem("VOIR TOUS");
        for (String choix : coloneName ) comboBox1.addItem(choix);
    }
    private void insererColumnName(String tableName)
    {
        DefaultTableModel tableModel = new DefaultTableModel();
        table2.setModel(tableModel);
        String[] columnNames = AddColoneTable.retrieveColoneNames(tableName);
        tableModel.setColumnIdentifiers(columnNames);
        scrolTable.setViewportView(table2);
    }


    private void insererLignes(String tableName) {
        DefaultTableModel tableModel = (DefaultTableModel) table2.getModel();
        tableModel.setRowCount(0); // Pour supprimer toutes les lignes existantes dans le modèle

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Établir une connexion à la base de données
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Préparer la requête SQL pour récupérer toutes les lignes de la table
            String sqlQuery = "SELECT * FROM " + tableName;
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            // Récupérer les métadonnées de la table
            int columnCount = resultSet.getMetaData().getColumnCount();
            // Ajouter chaque ligne au modèle de tableau
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {

                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage()); // Gérer les exceptions correctement dans une application réelle
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage()); // Gérer les exceptions correctement dans une application réelle

            }
        }

    }
    private void insererLignes(String tableName, String cherchetype, String chercheValue) {
        DefaultTableModel tableModel = (DefaultTableModel) table2.getModel();
        tableModel.setRowCount(0); // Pour supprimer toutes les lignes existantes dans le modèle

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Établir une connexion à la base de données
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            // Préparer la requête SQL pour récupérer toutes les lignes de la table
            String sqlQuery = "SELECT " + cherchetype + " FROM " + tableName + " WHERE ROWNUM <= 1";
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String columnType = "";
            for (int i = 1; i <= columnCount; i++) {
                 columnType = metaData.getColumnTypeName(i);
            }
            if (chercheValue.trim().isEmpty())
            {
                sqlQuery = "SELECT * FROM " + tableName + " WHERE "+cherchetype+" is null";
            }
            else
            {
                if (columnType == "DATE")
                {
                    sqlQuery = "SELECT * FROM " + tableName + " WHERE "+cherchetype+" = TO_DATE('"+chercheValue+"','YYYY-MM-DD')";
                }
                else
                {
                    if (columnType == "NUMBER")
                    {
                        sqlQuery = "SELECT * FROM " + tableName + " WHERE "+cherchetype+" = "+chercheValue;
                    }
                    else
                    {
                        sqlQuery = "SELECT * FROM " + tableName + " WHERE "+cherchetype+" = '"+chercheValue+"'";
                    }
                }
            }


            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            // Stocker le résultat de la condition
            boolean hasRecords = resultSet.next();

            // Utiliser la variable dans la condition if et dans la boucle while
            if (!hasRecords) {
                JOptionPane.showMessageDialog(null, "Aucun enregistrement trouvé !");
            } else {
                // Récupérer les métadonnées de la table
                columnCount = resultSet.getMetaData().getColumnCount();

                // Ajouter chaque ligne au modèle de tableau
                do {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = resultSet.getObject(i);
                    }
                    tableModel.addRow(rowData);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }



}
