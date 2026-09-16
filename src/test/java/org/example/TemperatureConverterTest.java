package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemperatureConverterTest {

    TemperatureConverter converter = new TemperatureConverter();

    @Test
    void testFahrenheitToCelsius(){
        assertEquals(0, converter.fahrenheitToCelsius(32), 0.01);
        assertEquals(100, converter.fahrenheitToCelsius(212), 0.01);
        assertEquals(-17.78, converter.fahrenheitToCelsius(0), 0.01);
    }

    @Test
    void testCelsiusToFahrenheit(){
        assertEquals(32, converter.celsiusToFahrenheit(0), 0.01);
        assertEquals(212, converter.celsiusToFahrenheit(100), 0.01);
        assertEquals(98.6, converter.celsiusToFahrenheit(37), 0.01);
    }

    @Test
    void testIsExtremeTemperature(){
        assertTrue(converter.isExtremeTemperature(-41));
        assertTrue(converter.isExtremeTemperature(51));
        assertFalse(converter.isExtremeTemperature(-40));
        assertFalse(converter.isExtremeTemperature(50));
        assertFalse(converter.isExtremeTemperature(25));
    }

    @Test
    void testKelvinToCelsius() {
        assertEquals(26.85, converter.kelvinToCelsius(300), 0.01);
        assertEquals(0, converter.kelvinToCelsius(273.15), 0.01);
        assertEquals(-273.15, converter.kelvinToCelsius(0), 0.01);
    }
}
