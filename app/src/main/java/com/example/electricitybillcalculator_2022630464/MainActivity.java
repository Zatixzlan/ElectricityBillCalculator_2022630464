package com.example.electricitybillcalculator_2022630464;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electricitybillcalculator_2022630464.db.BillRecord;
import com.example.electricitybillcalculator_2022630464.db.DBHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spMonth, spRebate;
    private EditText etKwh;
    private TextView tvTotal, tvFinal;
    private DBHelper db;
    private double lastTotal = -1;
    private double lastFinal = -1;
    private int lastKwh = 0;
    private int lastRebate = 0;
    private String lastMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spMonth = findViewById(R.id.spMonth);
        spRebate = findViewById(R.id.spRebate);
        etKwh = findViewById(R.id.etKwh);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);

        db = new DBHelper(this);

        setupMonthSpinner();
        setupRebateSpinner(); // 0% - 5%

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculate());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveRecord());
        findViewById(R.id.btnHistory).setOnClickListener(
                v -> startActivity(new Intent(this, HistoryActivity.class))
        );
        findViewById(R.id.btnAbout).setOnClickListener(
                v -> startActivity(new Intent(this, AboutActivity.class))
        );
    }

    private void setupMonthSpinner() {
        String[] months = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                months
        );
        adapter.setDropDownViewResource(R.layout.dropdown);
        spMonth.setAdapter(adapter);
    }

    private void setupRebateSpinner() {
        String[] rebateOptions = {"0%", "1%", "2%", "3%", "4%", "5%"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                rebateOptions
        );
        adapter.setDropDownViewResource(R.layout.dropdown);
        spRebate.setAdapter(adapter);
        spRebate.setSelection(0);

        spRebate.setSelection(0);
    }

    private void calculate() {
        etKwh.setError(null);
        String kwhText = etKwh.getText().toString().trim();

        if (TextUtils.isEmpty(kwhText)) {
            etKwh.setError("Please enter kWh used");
            etKwh.requestFocus();
            return;
        }

        int kwh;
        try {
            kwh = Integer.parseInt(kwhText);
        } catch (Exception e) {
            etKwh.setError("Invalid number");
            etKwh.requestFocus();
            return;
        }

        if (kwh <= 0) {
            etKwh.setError("Must be > 0");
            etKwh.requestFocus();
            return;
        }

        int rebate = getSelectedRebateInt(); // 0..5 as int (DB column is INTEGER)
        double total = calcTotalCharges(kwh);
        double finalCost = total - (total * rebate / 100.0);

        tvTotal.setText(String.format(Locale.getDefault(),
                "Total Charges: RM %.2f", total));

        tvFinal.setText(String.format(Locale.getDefault(),
                "Final Cost: RM %.2f", finalCost));

        // store last values for SAVE
        lastMonth = spMonth.getSelectedItem().toString();
        lastKwh = kwh;
        lastRebate = rebate;
        lastTotal = total;
        lastFinal = finalCost;
    }

    private void saveRecord() {
        if (lastTotal < 0) {
            Toast.makeText(this, "Please calculate first", Toast.LENGTH_SHORT).show();
            return;
        }

        BillRecord r = new BillRecord();
        r.month = lastMonth;
        r.kwh = lastKwh;
        r.rebatePercent = lastRebate;      // int
        r.totalCharges = lastTotal;
        r.finalCost = lastFinal;
        r.createdAt = System.currentTimeMillis();

        long id = db.insertBill(r);

        if (id > 0) {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            resetForm();
        } else {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void resetForm() {
        spMonth.setSelection(0);
        spRebate.setSelection(0);
        etKwh.setText("");

        tvTotal.setText("Total Charges: RM 0.00");
        tvFinal.setText("Final Cost: RM 0.00");

        lastTotal = -1;
        lastFinal = -1;
        lastKwh = 0;
        lastRebate = 0;
        lastMonth = "";

        etKwh.requestFocus();
    }

    private int getSelectedRebateInt() {
        String selected = spRebate.getSelectedItem().toString(); // "3%"
        selected = selected.replace("%", "");
        try {
            return Integer.parseInt(selected); // 0..5
        } catch (Exception e) {
            return 0;
        }
    }

    private double calcTotalCharges(int kwh) {
        double total = 0;

        int b1 = Math.min(kwh, 200);
        total += b1 * 0.218;
        kwh -= b1;

        if (kwh > 0) {
            int b2 = Math.min(kwh, 100);
            total += b2 * 0.334;
            kwh -= b2;
        }

        if (kwh > 0) {
            int b3 = Math.min(kwh, 300);
            total += b3 * 0.516;
            kwh -= b3;
        }

        if (kwh > 0) {
            total += kwh * 0.546;
        }

        return total;
    }
}
