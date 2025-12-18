package com.example.electricitybillcalculator_2022630464.db;

public class BillRecord {

    public long id;
    public String month;
    public int kwh;
    public int rebatePercent;
    public double totalCharges;
    public double finalCost;
    public long createdAt;

    public BillRecord() {
    }
}