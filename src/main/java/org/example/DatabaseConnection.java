package org.example;

import java.sql.*;

public class DatabaseConnection {

    public static void main(String[] args) {


        final String connectionString = "jdbc:sqlserver://itnt0005:1433;encrypt=true;trustServerCertificate=true;databaseName=WKB4_DB2_Projekt;user=wkb4;password=wkb4";

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

                con = DriverManager.getConnection(connectionString);

                String SQL = "SELECT * FROM dbo.thrait02_test";
                stmt = con.prepareStatement(SQL);
                rs = stmt.executeQuery();

                while (rs.next()){
                    System.out.println(rs.getString("test_name")+ rs.getString("test_number"));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) try{ rs.close();} catch(Exception e){}
            if (stmt != null) try{ stmt.close();} catch(Exception e){}
            if (con != null) try{ con.close();} catch(Exception e){}
        }
    }
}
