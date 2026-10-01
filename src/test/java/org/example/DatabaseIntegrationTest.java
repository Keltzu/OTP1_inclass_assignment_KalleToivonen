package org.example;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseIntegrationTest {

    @Test
    void testDBConnection() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testGetAllUnits() throws SQLException {
        TemperatureUnitDAO dao = new TemperatureUnitDAO();
        List<TemperatureUnit> units = dao.getAllUnits();
        assertNotNull(units);
        assertTrue(units.size() >= 3);
    }

    @Test
    void testGetUnitById() throws SQLException {
        TemperatureUnitDAO dao = new TemperatureUnitDAO();
        TemperatureUnit unit = dao.getUnitById(1);
        assertNotNull(unit);
    }

    @Test
    void testInsertAndRetrieveRecord() throws SQLException {
        TempRecordDAO dao = new TempRecordDAO();
        TempRecord record = new TempRecord(50.0, 1, 122.0, 2);

        int newId = dao.insertRecord(record);
        assertTrue(newId > 0);

        List<TempRecord> records = dao.getAllRecords();
        assertFalse(records.isEmpty());
    }

    @Test
    void testTemperatureUnitGetters() {
        TemperatureUnit unit = new TemperatureUnit(1, "Celsius");
        assertEquals(1, unit.getId());
        assertEquals("Celsius", unit.getName());
        assertEquals("Celsius", unit.toString());
    }

    @Test
    void testTempRecordGetters() {
        TempRecord record = new TempRecord(10.0, 1, 50.0, 2);
        assertEquals(10.0, record.getInputValue());
        assertEquals(1, record.getInputUnitId());
        assertEquals(50.0, record.getOutputValue());
        assertEquals(2, record.getOutputUnitId());
    }
}