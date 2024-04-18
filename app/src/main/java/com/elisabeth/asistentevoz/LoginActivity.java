package com.elisabeth.asistentevoz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elisabeth.asistentevoz.repository.ConexionBD;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private static ConexionBD conexionBD = new ConexionBD();

    EditText username;
    EditText password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //inicioSesion(username.getText().toString(),password.getText().toString());
            }
        });

    }

    public void inicioSesion(String user,String password){
        new ConexionBD().execute();
    }
}