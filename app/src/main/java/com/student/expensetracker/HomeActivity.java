package com.student.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    TextView tvBalance, tvIncome, tvExpense;
    Button btnAddExpense, btnLogout;

    RecyclerView recyclerExpenses;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ValueEventListener expenseListener;

    ArrayList<Expense> expenseList;
    ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        tvBalance = findViewById(R.id.tvBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);

        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        // RecyclerView setup
        expenseList = new ArrayList<>();

        recyclerExpenses.setLayoutManager(
                new LinearLayoutManager(this));

        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerExpenses.setAdapter(expenseAdapter);

        // Firebase database reference
        String uid = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance(
                        "https://student-expense-tracker-7314c-default-rtdb.asia-southeast1.firebasedatabase.app"
                ).getReference("Users")
                .child(uid)
                .child("Expenses");

        loadTransactions();

        // Open Add Expense screen
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    AddEditExpenseActivity.class
            );
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();

            Intent intent = new Intent(
                    HomeActivity.this,
                    MainActivity.class
            );

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    private void loadTransactions() {

        expenseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                expenseList.clear();

                double totalIncome = 0;
                double totalExpense = 0;

                for (DataSnapshot data : snapshot.getChildren()) {

                    Expense expense = data.getValue(Expense.class);

                    if (expense != null) {

                        expenseList.add(expense);

                        if ("Income".equals(expense.getType())) {
                            totalIncome += expense.getAmount();
                        } else {
                            totalExpense += expense.getAmount();
                        }
                    }
                }

                double balance = totalIncome - totalExpense;

                tvIncome.setText(formatAmount(totalIncome));
                tvExpense.setText(formatAmount(totalExpense));
                tvBalance.setText(formatAmount(balance));

                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(HomeActivity.this,
                        "Database Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        databaseReference.addValueEventListener(expenseListener);
    }

    private String formatAmount(double amount) {
        return String.format(
                Locale.getDefault(),
                "৳ %.2f",
                amount
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseReference != null && expenseListener != null) {
            databaseReference.removeEventListener(expenseListener);
        }
    }
}
