package org.example;

import java.sql.*;

public class DatabaseConnection {

    private static final String connectionString = "jdbc:sqlserver://itnt0005:1433;encrypt=true;trustServerCertificate=true;databaseName=WKB4_DB2_Projekt;user=wkb4;password=wkb4";

    public static  Connection getConnection() throws SQLException{
          return DriverManager.getConnection(connectionString);
    }
    public static boolean isConnected(){
        try (Connection connection = getConnection()){
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean validateLogin(String username, String password) {
        boolean isValid = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM dbo.thrait02_userCred WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                isValid = true;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return isValid;
    }
}
