package com.example.electricitybillcalculator_2022630464.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "electricity_bill.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_BILL = "bill";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_BILL + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "month TEXT," +
                "kwh INTEGER," +
                "rebate INTEGER," +
                "total REAL," +
                "final REAL," +
                "createdAt INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL);
        onCreate(db);
    }

    // INSERT
    public long insertBill(BillRecord r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", r.month);
        cv.put("kwh", r.kwh);
        cv.put("rebate", r.rebatePercent);
        cv.put("total", r.totalCharges);
        cv.put("final", r.finalCost);
        cv.put("createdAt", r.createdAt);
        return db.insert(TABLE_BILL, null, cv);
    }

    // GET ALL
    public List<BillRecord> getAllBills() {
        List<BillRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_BILL + " ORDER BY createdAt DESC",
                null
        );

        while (c.moveToNext()) {
            BillRecord r = new BillRecord();
            r.id = c.getLong(0);
            r.month = c.getString(1);
            r.kwh = c.getInt(2);
            r.rebatePercent = c.getInt(3);
            r.totalCharges = c.getDouble(4);
            r.finalCost = c.getDouble(5);
            r.createdAt = c.getLong(6);
            list.add(r);
        }
        c.close();
        return list;
    }

    // GET BY ID
    public BillRecord getBillById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_BILL + " WHERE id=?",
                new String[]{String.valueOf(id)}
        );

        if (c.moveToFirst()) {
            BillRecord r = new BillRecord();
            r.id = c.getLong(0);
            r.month = c.getString(1);
            r.kwh = c.getInt(2);
            r.rebatePercent = c.getInt(3);
            r.totalCharges = c.getDouble(4);
            r.finalCost = c.getDouble(5);
            r.createdAt = c.getLong(6);
            c.close();
            return r;
        }
        c.close();
        return null;
    }

    // DELETE
    public void deleteBill(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BILL, "id=?", new String[]{String.valueOf(id)});
    }
}

