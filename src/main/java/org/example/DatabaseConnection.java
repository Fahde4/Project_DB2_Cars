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
}
