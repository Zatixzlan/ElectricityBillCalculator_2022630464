package com.example.electricitybillcalculator_2022630464.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricitybillcalculator_2022630464.R;   // 🔥 INI
import com.example.electricitybillcalculator_2022630464.db.BillRecord;

import java.util.List;
import java.util.Locale;


public class BillAdapter extends RecyclerView.Adapter<BillAdapter.VH> {

    public interface OnItemClick {
        void onClick(BillRecord record);
    }

    private final List<BillRecord> data;
    private final OnItemClick listener;

    public BillAdapter(List<BillRecord> data, OnItemClick listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bill, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BillRecord r = data.get(position);

        holder.tvMonth.setText(r.month);
        holder.tvFinalCost.setText(String.format(Locale.getDefault(),
                "Final Cost: RM %.2f", r.finalCost));

        holder.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvMonth, tvFinalCost;

        VH(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvFinalCost = itemView.findViewById(R.id.tvFinalCost);
        }
    }
}