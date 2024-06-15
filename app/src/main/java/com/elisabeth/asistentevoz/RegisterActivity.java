package com.elisabeth.asistentevoz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Models.UserDTO;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;
    EditText name;
    Button registerButton;

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        name = findViewById(R.id.nombre);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean register = registrarUsuario(username.getText().toString(),password.getText().toString(),email.getText().toString(),name.getText().toString());
                if(register){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    private boolean registrarUsuario(String username, String password, String email, String name) {
        try {
            URL url = new URL("http://192.168.149.28:8080/asistente/user/register");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonRegister = new JSONObject();
            jsonRegister.put("username",username);
            jsonRegister.put("userpassword",password);
            jsonRegister.put("email",email);
            jsonRegister.put("nombre",name);

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(jsonRegister.toString());
            osw.flush();

            int responseCode = connection.getResponseCode();
            Log.i("INFORMACION", String.valueOf(responseCode));
            if(responseCode == 200 || responseCode == 201){
                Toast.makeText(this, "REGISTRO COMPLETADO", Toast.LENGTH_SHORT).show();
                return true;
            }
            Toast.makeText(this, connection.getResponseMessage(), Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}