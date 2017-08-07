/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBHandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Shaafi
 */
public class DbConnector {

    private Connection mConnection = null;
    private Statement statement = null;
    private ResultSet result = null;
    private PreparedStatement pStatement = null;

    public DbConnector() {
    }

    public DbConnector(String dbName, String userName, String pass) {

        connectDatabase(dbName, userName, pass);

    }

    public boolean connectDatabase(String dbName, String userName, String pass) {

        try {

            String user = userName;
            String password = pass;
            String url = "jdbc:mysql://localhost/" + dbName + "?useSSL=false";

            Class.forName("com.mysql.jdbc.Driver");
            mConnection = DriverManager.
                    getConnection(url, user, password);

            System.out.println("Successfully Connected..");

            return true;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Problem in Connection: " + e.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Sorry! Connection Failed!\n" + e.getLocalizedMessage());
        }

        return false;

    }

    public void closeConnection() {
        if (mConnection != null) {
            try {
                mConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<String> getTableNames() {
        List<String> tables = new ArrayList<>();
//        try {
//
//            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
//                tables.add(result.getMetaData().getColumnName(i));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
//        }

        ResultSet rs;
        try {
            DatabaseMetaData md = mConnection.getMetaData();
            rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                System.out.println(rs.getString(3));
                tables.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tables;

    }

    public ArrayList<String[]> getTableData(String tableName) {

        ArrayList<String[]> records = new ArrayList<>();
        try {

            String query = "SELECT * FROM " + tableName;
            pStatement = mConnection.prepareStatement(query);

            result = pStatement.executeQuery();

            String[] colName = new String[result.getMetaData().getColumnCount()];
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                colName[i] = result.getMetaData().getColumnName(i + 1); // as column index number starts from 1
            }

            records.add(colName);

            for (String cc : colName) {
                System.out.print(cc + " ");
            }
            System.out.println("");

            while (result.next()) {
                int count = 0;
                String[] r = new String[result.getMetaData().getColumnCount()];
                for (String cc : colName) {
                    r[count++] = result.getString(cc);
                }

                records.add(r);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in getTableData!\n" + e.getLocalizedMessage());
        }

        return records;
    }

    public ArrayList<String> getColumns(String selectedTable) {
        ArrayList<String> cols = new ArrayList<>();
        try {

            String query = "SELECT column_name FROM information_schema.columns WHERE table_name=?";
            pStatement = mConnection.prepareStatement(query);
            pStatement.setString(1, selectedTable);

            result = pStatement.executeQuery();

            while (result.next()) {
                cols.add(result.getString("column_name"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in getColumns!\n" + e.getLocalizedMessage());
        }

        return cols;
    }

    public boolean insertNewData(String tableName, ArrayList<String> newRowDatas) {
        try {

            String query = "INSERT INTO " + tableName + " VALUES ( ";

            for (int i = 0; i < newRowDatas.size(); i++) {
                query += "?";

                if (i + 1 != newRowDatas.size()) {
                    query += ", ";
                }
            }
            query += " )";

            System.out.println("query: " + query);

            pStatement = mConnection.prepareStatement(query);

            for (int i = 1; i <= newRowDatas.size(); i++) {
                pStatement.setString(i, newRowDatas.get(i - 1));

                System.out.println("row: " + newRowDatas.get(i - 1));
            }

            int res = pStatement.executeUpdate();

            if (res > 0) {
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in insertNewData!\n" + e.getLocalizedMessage());
        }

        return false;
    }

    public boolean deleteData(String tableName, String field, String value) {
        try {

            String query = "DELETE FROM " + tableName + " WHERE " + field + "=?";

            pStatement = mConnection.prepareStatement(query);

            pStatement.setString(1, value);

            System.out.println("query: " + query);

            int res = pStatement.executeUpdate();

            if (res > 0) {
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in deleteData!\n" + e.getLocalizedMessage());
        }

        return false;
    }

    public boolean addColumn(String tableName, String colName) {
        try {

            String[] cols = colName.split(",");

            String colType;

            if (cols.length < 2) {
                colType = "varchar(300)";
            } else {
                colType = cols[1];
            }

            colName = cols[0].replace(" ", "_");

            String query = "ALTER TABLE " + tableName + " ADD COLUMN " + colName.toLowerCase() + " " + colType;

            pStatement = mConnection.prepareStatement(query);

            System.out.println("query: " + query);
            pStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in addColumn!\n" + e.getLocalizedMessage());
        }

        return false;
    }

    public boolean deleteColumn(String tableName, String selectedColumn) {
               try {

            String query = "ALTER TABLE " + tableName + " DROP COLUMN " + selectedColumn;

            pStatement = mConnection.prepareStatement(query);

            System.out.println("query: " + query);
            pStatement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Column Deleted!");
            return true; 

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in addColumn!\n" + e.getLocalizedMessage());
        }

            JOptionPane.showMessageDialog(null, "Column not deleted!");
            
            return false;
    }

}
