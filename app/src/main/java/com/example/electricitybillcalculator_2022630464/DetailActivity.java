package com.example.electricitybillcalculator_2022630464;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electricitybillcalculator_2022630464.db.DBHelper;
import com.example.electricitybillcalculator_2022630464.db.BillRecord;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private long billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        billId = getIntent().getLongExtra(HistoryActivity.EXTRA_BILL_ID, -1);

        TextView tvMonth  = findViewById(R.id.tvDMonth);
        TextView tvKwh    = findViewById(R.id.tvDKwh);
        TextView tvTotal  = findViewById(R.id.tvDTotal);
        TextView tvRebate = findViewById(R.id.tvDRebate);
        TextView tvFinal  = findViewById(R.id.tvDFinal);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);

        DBHelper db = new DBHelper(this);
        BillRecord r = db.getBillById(billId);

        if (r == null) {
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvMonth.setText(r.month);
        tvKwh.setText(String.valueOf(r.kwh));
        tvTotal.setText(String.format(Locale.getDefault(), "RM %.2f", r.totalCharges));
        tvRebate.setText(r.rebatePercent + "%");
        tvFinal.setText(String.format(Locale.getDefault(), "RM %.2f", r.finalCost));

        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Bill")
                        .setMessage("Are you sure you want to delete this record?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.deleteBill(billId);
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
