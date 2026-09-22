package org.example;

public class Main {
    public static void main(String[] args) {
        TemperatureConverter converter = new TemperatureConverter();

        System.out.println("Temperature Converter running in Docker!");
        System.out.println("0 C to F: " + converter.celsiusToFahrenheit(0));
        System.out.println("212 F to C: " + converter.fahrenheitToCelsius(212));
        System.out.println("300 K to C: " + converter.kelvinToCelsius(300));
        System.out.println("Is -50 extreme? " + converter.isExtremeTemperature(-50));
    }
}