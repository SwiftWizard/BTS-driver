package com.rk.bts_busdriversapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty()){
                    etEmail.setError(getText(R.string.et_not_filled_in_warning));
                    etEmail.requestFocus();
                }

                if(password.isEmpty()){
                    etPassword.setError(getText(R.string.et_not_filled_in_warning));
                    etPassword.requestFocus();
                }

                if(!email.isEmpty() && !password.isEmpty()){

                    FirebaseAuth fbAuth = FirebaseAuth.getInstance();

                    fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), R.string.login_failed_toast, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), R.string.fields_not_filled_in_toast, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}