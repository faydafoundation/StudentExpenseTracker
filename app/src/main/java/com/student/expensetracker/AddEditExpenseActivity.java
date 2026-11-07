package com.student.expensetracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddEditExpenseActivity extends AppCompatActivity {

    EditText etTitle, etAmount;
    Spinner spCategory, spType;
    Button btnSaveExpense;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        mAuth = FirebaseAuth.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        spType = findViewById(R.id.spType);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);

        String[] categories = {
                "Food",
                "Transport",
                "Education",
                "Shopping",
                "Entertainment",
                "Other"
        };

        String[] types = {"Expense", "Income"};

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        types
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spType.setAdapter(typeAdapter);

        btnSaveExpense.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {

        String title = etTitle.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Enter transaction title");
            etTitle.requestFocus();
            return;
        }

        if (amountText.isEmpty()) {
            etAmount.setError("Enter amount");
            etAmount.requestFocus();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            etAmount.setError("Enter a valid amount");
            etAmount.requestFocus();
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            etAmount.setError("Amount must be greater than zero");
            etAmount.requestFocus();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance(
                        "https://student-expense-tracker-7314c-default-rtdb.asia-southeast1.firebasedatabase.app"
                ).getReference("Users")
                .child(uid)
                .child("Expenses");

        Map<String, Object> transaction = new HashMap<>();

        transaction.put("title", title);
        transaction.put("amount", amount);
        transaction.put("category", spCategory.getSelectedItem().toString());
        transaction.put("type", spType.getSelectedItem().toString());
        transaction.put("date", System.currentTimeMillis());

        btnSaveExpense.setEnabled(false);

        databaseReference.push().setValue(transaction)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Transaction Saved Successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnSaveExpense.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Save Failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
