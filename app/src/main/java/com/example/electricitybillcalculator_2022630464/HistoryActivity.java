package com.example.electricitybillcalculator_2022630464;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricitybillcalculator_2022630464.db.BillRecord;
import com.example.electricitybillcalculator_2022630464.db.DBHelper;
// kalau BillAdapter dalam package adapter, tukar import ikut lokasi sebenar:
import com.example.electricitybillcalculator_2022630464.adapter.BillAdapter;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    public static final String EXTRA_BILL_ID = "BILL_ID";

    private RecyclerView rvBills;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvBills = findViewById(R.id.rvBills);
        rvBills.setLayoutManager(new LinearLayoutManager(this));

        db = new DBHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadBills();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills(); // refresh bila balik dari Detail (contoh lepas delete)
    }

    private void loadBills() {
        List<BillRecord> records = db.getAllBills();

        if (records == null || records.isEmpty()) {
            Toast.makeText(this, "No records yet. Save a bill first!", Toast.LENGTH_SHORT).show();
        }

        BillAdapter adapter = new BillAdapter(records, record -> {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(EXTRA_BILL_ID, record.id);
            startActivity(i);
        });

        rvBills.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


