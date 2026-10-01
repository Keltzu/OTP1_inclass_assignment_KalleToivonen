package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {

    public int insertRecord(TempRecord record) throws SQLException {
        String sql = "INSERT INTO temp_record (input_value, input_unit_id, output_value, output_unit_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, record.getInputValue());
            stmt.setInt(2, record.getInputUnitId());
            stmt.setDouble(3, record.getOutputValue());
            stmt.setInt(4, record.getOutputUnitId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<TempRecord> getAllRecords() throws SQLException {
        List<TempRecord> records = new ArrayList<>();
        String sql = "SELECT id, input_value, input_unit_id, output_value, output_unit_id FROM temp_record ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                records.add(new TempRecord(
                        rs.getInt("id"),
                        rs.getDouble("input_value"),
                        rs.getInt("input_unit_id"),
                        rs.getDouble("output_value"),
                        rs.getInt("output_unit_id")
                ));
            }
        }
        return records;
    }
}