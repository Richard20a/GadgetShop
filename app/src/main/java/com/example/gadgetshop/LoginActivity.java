package com.example.gadgetshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    EditText editTextEmail2;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 345) {
            finish();
        }

        editTextEmail2 = findViewById(R.id.editTextEmail2);
        editTextPassword = findViewById(R.id.editTextPassword2);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String userName = editTextEmail2.getText().toString();
        String password = editTextPassword.getText().toString();


        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "User loged in successfully");
                startShopping();
            } else {
                Log.d(LOG_TAG, "User log in fail");
                Toast.makeText(LoginActivity.this, "User log in fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startShopping() {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }
}