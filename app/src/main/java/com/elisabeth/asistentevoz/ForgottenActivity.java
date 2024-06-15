package com.elisabeth.asistentevoz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ForgottenActivity extends AppCompatActivity {

    EditText username;
    EditText email;
    Button recoveryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotten_layout);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        recoveryButton = findViewById(R.id.recoveryButton);

        recoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean recovery = recovery(username.getText().toString(),email.getText().toString());
                if (recovery){
                    Intent intent = new Intent(ForgottenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean recovery(String username, String email) {
        try {
            URL url = new URL("http://192.168.149.28:8080/asistente/user/forgotten");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonRegister = new JSONObject();
            jsonRegister.put("username",username);
            jsonRegister.put("email",email);

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(jsonRegister.toString());
            osw.flush();

            int responseCode = connection.getResponseCode();
            Log.i("INFORMACION", String.valueOf(responseCode));
            if(responseCode == 200 ){
                Toast.makeText(this, "Comprueba el correo", Toast.LENGTH_SHORT).show();
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