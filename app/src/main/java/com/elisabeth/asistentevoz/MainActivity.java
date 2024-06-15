package com.elisabeth.asistentevoz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import Models.ChangePasswordRequest;
import Models.User;
import Models.UserDTO;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode=1;

    private SpeechRecognizer speechRecognizer;
    AlertDialog.Builder alertSpeechDialog;
    AlertDialog alertDialog;
    ImageButton imageButton;

    TextView textView;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String username;
        UserDTO user = null;
        if (bundle.getString("username") != null){
            StringBuilder json = new StringBuilder();
            URL url = null;
            try {
                url = new URL("http://192.168.149.28:8080/asistente/user/"+bundle.getString("username"));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String entrada;
                    while ((entrada = in.readLine()) != null) {
                        json.append(entrada);
                    }
                }

                System.out.println(json.toString());
                Gson gson = new Gson();
                user = gson.fromJson(json.toString(), UserDTO.class);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        User.setId(user.getId());
        User.setUsername(user.getUsername());
        User.setEmail(user.getEmail());
        User.setNombre(user.getNombre());
        User.setUserpassword(user.getUserpassword());

        imageButton = findViewById(R.id.imgbtnRecord);
        textView = findViewById(R.id.text);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    Locale locale = new Locale("es_ES");
                    textToSpeech.setLanguage(Locale.forLanguageTag(locale.getLanguage()));
                }
            }
        });

        textToSpeech.speak("HOLA " + User.getNombre(),TextToSpeech.QUEUE_FLUSH,null);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechIntent = new Intent((RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alertcustom,viewGroup,false);

                alertSpeechDialog = new AlertDialog.Builder(MainActivity.this);
                alertSpeechDialog.setMessage("Escuchando...");
                alertSpeechDialog.setView(dialogView);
                alertDialog = alertSpeechDialog.create();
                alertDialog.show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> arrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                textView.setText(arrayList.get(0));
                alertDialog.dismiss();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                speechRecognizer.startListening(speechIntent);
                return false;
            }
        });

        speechRecognizer.stopListening();




        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String getText= s.toString();
                textToSpeech.speak(getText,TextToSpeech.QUEUE_FLUSH,null);
                Log.i("INFO",textView.getText().toString());
                Intent intent = new Intent(MainActivity.this,GestorComandos.class);
                intent.putExtra("comando",textView.getText().toString());
                startActivity(intent);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==RecordAudioRequestCode && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permisos concedidos",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (R.id.changePassword == id){
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (R.id.logout == id) {
            User user = new User();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contex_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}