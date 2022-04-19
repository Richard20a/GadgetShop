package com.example.gadgetshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private FirebaseAuth mAuth;
    EditText editTextPersonName;
    EditText editTextEmailAddress;
    EditText editTextPhone;
    EditText editTextPostalAddress;
    EditText editTextPassword;
    EditText editTextPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 345) {
            finish();
        }

        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPostalAddress = findViewById(R.id.editTextPostalAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordAgain =findViewById(R.id.editTextPasswordAgain);

        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view){
        String userName = editTextPersonName.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        String phone = editTextPhone.getText().toString();
        String address = editTextPostalAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordAgain.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "Nem azonos a jelszó és a megerősítése.");
            //onRestart();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Log.d(LOG_TAG, "Felhasználó sikeresen létrehozva!");
                startShopping();
            }
            else {
                Log.d(LOG_TAG, "Felhasználó létrehozása sikertelen!");
                Toast.makeText(RegisterActivity.this, "Felhasználó létrehozása sikertelen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view){
        finish();
    }

    private void startShopping(){
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }

}