import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Table des noms de tables");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);

        tableModel.addColumn("Nom de la Table");

        retrieveTableNames(tableModel);
        JButton editButton = new JButton("Editer");
        JButton deleteButton = new JButton("Supprimer");
        JButton addButton = new JButton("Ajouter Table");


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame dialog = new JFrame();
                AjouterTable table = new AjouterTable(dialog);
                tableModel.setRowCount(0);
                retrieveTableNames(tableModel);
            }
        });
        // Ajoutez ici le code pour définir des actions pour les boutons (éditer, supprimer, voir).
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code pour l'action d'édition
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String tableName = (String) table.getValueAt(selectedRow, 0);

                    // Fermer le JFrame courant
                    frame.dispose();

                    // Afficher le nouvel EditerTable
                    EditerTable editerTable = new EditerTable(tableName);
                } else {
                    JOptionPane.showMessageDialog(null, "Aucune table n'est sélectionnée.");
                }
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code pour l'action de suppression
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String tableName = (String) table.getValueAt(selectedRow, 0);
                    // Ajoutez le code pour l'action de suppression ici avec le nom de la table sélectionnée
                    try {
                        String requette = "DROP TABLE "+tableName;
                        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(requette);
                        statement.close();
                        connection.close();
                        JOptionPane.showMessageDialog(null,"La table "+tableName+" été supprimer avec succes");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,"erreur à la suppression de la table :"+ex.getMessage());
                    }
                    tableModel.setRowCount(0);
                    retrieveTableNames(tableModel);
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Aucun table ni selectionner.");
                }

            }
        });



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void retrieveTableNames(DefaultTableModel tableModel) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT table_name FROM user_tables");

            while (resultSet.next()) {
                Object[] rowData = new Object[]{resultSet.getString("table_name")};
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"erreur à la recuperation des noms des table :"+ex.getMessage());
        }
    }
}
