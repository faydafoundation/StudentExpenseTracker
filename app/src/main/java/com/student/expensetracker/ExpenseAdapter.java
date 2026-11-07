package com.student.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final ArrayList<Expense> expenseList;

    public ExpenseAdapter(ArrayList<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ExpenseViewHolder holder, int position) {

        Expense expense = expenseList.get(position);

        holder.tvItemTitle.setText(expense.getTitle());
        holder.tvItemCategory.setText(
                expense.getCategory() + " • " + expense.getType()
        );

        String sign = "Income".equals(expense.getType()) ? "+" : "-";

        holder.tvItemAmount.setText(
                sign + " ৳ " + String.format(
                        java.util.Locale.getDefault(),
                        "%.2f", expense.getAmount())
        );
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle, tvItemCategory, tvItemAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
        }
    }
}
