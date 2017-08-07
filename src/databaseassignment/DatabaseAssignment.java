/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseassignment;

/**
 *
 * @author Shaafi
 */
public class DatabaseAssignment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

//        DbConnector connector = new DbConnector("university_database", "root", "");
        MainJFrame mainJFrame = new MainJFrame();

        // center the jframe on screen
        mainJFrame.setLocationRelativeTo(null);
        mainJFrame.setVisible(true);

    }

}
