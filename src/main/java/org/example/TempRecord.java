package org.example;

public class TempRecord {

    private int id;
    private double inputValue;
    private int inputUnitId;
    private double outputValue;
    private int outputUnitId;

    public TempRecord(double inputValue, int inputUnitId, double outputValue, int outputUnitId) {
        this.inputValue = inputValue;
        this.inputUnitId = inputUnitId;
        this.outputValue = outputValue;
        this.outputUnitId = outputUnitId;
    }

    public TempRecord(int id, double inputValue, int inputUnitId, double outputValue, int outputUnitId) {
        this.id = id;
        this.inputValue = inputValue;
        this.inputUnitId = inputUnitId;
        this.outputValue = outputValue;
        this.outputUnitId = outputUnitId;
    }

    public int getId() {
        return id;
    }

    public double getInputValue() {
        return inputValue;
    }

    public int getInputUnitId() {
        return inputUnitId;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public int getOutputUnitId() {
        return outputUnitId;
    }
}