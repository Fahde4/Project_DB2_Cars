package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Queries {

    public static ResultSet retrieveDataFromDatabase() throws SQLException {
        String query = "SELECT * FROM dbo.thrait02_Autos";
        Connection connection = DatabaseConnection.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static boolean saveCarData(String id, String brand, String model, String motorisierung, int year, String chassis, int ps, String fuelEcon, int vMax) {
        String callProcedure = "{call dbo.thrait02_AddCar(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmt = connection.prepareCall(callProcedure)) {
            stmt.setString(1, id);
            stmt.setString(2, brand);
            stmt.setString(3, model);
            stmt.setString(4, motorisierung);
            stmt.setInt(5, year);
            stmt.setString(6, chassis);
            stmt.setInt(7, ps);
            stmt.setString(8, fuelEcon);
            stmt.setInt(9, vMax);
            stmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<String> getBrandsFromDatabase() {
        ObservableList<String> brands = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT marke FROM dbo.thrait02_Autos";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                brands.add(resultSet.getString("marke"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    public static ObservableList<String> getModellFromDatabase() {
        ObservableList<String> brands = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT modell FROM dbo.thrait02_Autos";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                brands.add(resultSet.getString("modell"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    public static ObservableList<String> getFormFromDatabase() {
        ObservableList<String> brands = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT karosserie FROM dbo.thrait02_Autos";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                brands.add(resultSet.getString("karosserie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    public static ResultSet getStrongestCar(String brand, String model, Integer year, String chassis) {
        String query = "SELECT * FROM dbo.thrait02_functionPS(?, ?, ?, ?)";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, brand);
            stmt.setString(2, model);
            if (year != null) {
                stmt.setInt(3, year);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, chassis);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getLowestConsumptionCar(String brand, String model, Integer year, String chassis) {
        String query = "SELECT * FROM dbo.thrait02_functionVerbrauch(?, ?, ?, ?)";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, brand);
            stmt.setString(2, model);
            if (year != null) {
                stmt.setInt(3, year);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, chassis);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getBestConsumptionToPowerRatio(String brand, String model, Integer year, String chassis) {
        String query = "SELECT * FROM dbo.thrait02_functionverbrauchZuLiter(?, ?, ?, ?)";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, brand);
            stmt.setString(2, model);
            if (year != null) {
                stmt.setInt(3, year);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, chassis);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getSpecific(String brand, String model, Integer year, String chassis) {
        String query = "SELECT * FROM dbo.thrait02_functionGetSpecific(?, ?, ?, ?)";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, brand);
            stmt.setString(2, model);
            if (year != null) {
                stmt.setInt(3, year);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, chassis);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
