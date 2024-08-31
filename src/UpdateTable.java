import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateTable extends JDialog{
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb";
    private static final String USER = "hr";
    private static final String PASSWORD = "hr";

    private JComboBox columnChanger;
    private JTextField newValeur;
    private JComboBox columnCondition;
    private JTextField valeurCondition;
    private JPanel updatePanel;
    private JButton annulerButton;
    private JButton terminerButton;

    public UpdateTable(JFrame parent , String tableName)
    {
        super(parent);
        setTitle("Update une ligne");
        setContentPane(updatePanel);
        setMinimumSize(new Dimension(500,300));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ajouterChois(tableName,columnChanger);
        ajouterChois(tableName,columnCondition);
        terminerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String conditionChamp = (String) columnCondition.getSelectedItem();
                String champChanger = (String) columnChanger.getSelectedItem();
                String conditionValue = valeurCondition.getText();
                String valeur = newValeur.getText();
                String requete ;
                if(valeur.trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(null,"nouveau valeur ne peut pas être vide.");
                }
                else
                {
                    if (isNumeric(valeur))
                    {
                        requete = "UPDATE "+tableName+" SET "+champChanger+" = "+valeur;
                    }
                    else
                    {
                        requete = "UPDATE "+tableName+" SET "+champChanger+" = '"+valeur+"'";
                    }
                    if(conditionValue.trim().isEmpty())
                    {
                        requete += " WHERE "+conditionChamp+" is null" ;
                    }
                    else
                    {
                        if(isNumeric(conditionValue))
                        {
                            requete += " WHERE "+conditionChamp+" = "+conditionValue ;
                        }
                        else
                        {
                            requete += " WHERE "+conditionChamp+" = '"+conditionValue+"'" ;
                        }
                    }

                    try
                    {
                        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(requete);
                        statement.close();
                        connection.close();
                        JOptionPane.showMessageDialog(null,"la mise à jour est succes ! ");
                        dispose();
                    }
                    catch (SQLException ex)
                    {
                        JOptionPane.showMessageDialog(null,"erreur dans la mise à jour de champs "+ex.getMessage());
                    }
                }
            }
        });

        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
    private static void ajouterChois(String tableName,JComboBox comboBox1)
    {
        String[] coloneName = AddColoneTable.retrieveColoneNames(tableName);
        for (String choix : coloneName ) comboBox1.addItem(choix);
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}