package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemperatureUnitDAO {

    public List<TemperatureUnit> getAllUnits() throws SQLException {
        List<TemperatureUnit> units = new ArrayList<>();
        String sql = "SELECT id, name FROM temperature_unit";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                units.add(new TemperatureUnit(rs.getInt("id"), rs.getString("name")));
            }
        }
        return units;
    }

    public TemperatureUnit getUnitById(int id) throws SQLException {
        String sql = "SELECT id, name FROM temperature_unit WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TemperatureUnit(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }
}