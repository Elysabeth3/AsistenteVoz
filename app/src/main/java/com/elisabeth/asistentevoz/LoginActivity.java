package com.elisabeth.asistentevoz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import Models.LoginRequest;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginButton;

    TextView registerText;

    TextView forggotenPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        registerText = findViewById(R.id.signupText);
        forggotenPassword = findViewById(R.id.forggotenPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inicioSesion(username.getText().toString(),password.getText().toString())){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username",username.getText().toString());
                    startActivity(intent);
                }
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forggotenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgottenActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean inicioSesion(String user,String password){
        String addressIP = getIP();
        LoginRequest loginRequest = new LoginRequest(user,password, addressIP);
        try {
            URL url = new URL("http://192.168.149.28:8080/asistente/user/login");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonLogin = new JSONObject();
            jsonLogin.put("username",loginRequest.getUsername());
            jsonLogin.put("userpassword",loginRequest.getUserpassword());
            jsonLogin.put("ipAddress",loginRequest.getIpAddress());

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(jsonLogin.toString());
            osw.flush();

            int responseCode = connection.getResponseCode();
            Log.i("INFORMACION", String.valueOf(responseCode));
            if(responseCode == 200){
                return true;
            }

            forggotenPassword.setVisibility(View.VISIBLE);
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

    private String getIP(){
        List<InetAddress> addrs;
        String address = "";
        try{
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface intf : interfaces){
                addrs = Collections.list(intf.getInetAddresses());
                for(InetAddress addr : addrs){
                    if(!addr.isLoopbackAddress() && addr instanceof Inet4Address){
                        address = addr.getHostAddress().toUpperCase(new Locale("es", "ES"));
                    }
                }
            }
        }catch (Exception e){
            Log.w("ERROR", "Ex getting IP value " + e.getMessage());
        }
        return address;
    }
}