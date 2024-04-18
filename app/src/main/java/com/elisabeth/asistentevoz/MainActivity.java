package com.elisabeth.asistentevoz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode=1;

    private SpeechRecognizer speechRecognizer;
    AlertDialog.Builder alertSpeechDialog;
    AlertDialog alertDialog;
    EditText editText;
    ImageButton imageButton;

    TextView textView;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        imageButton = findViewById(R.id.imgbtnRecord);
        textView = findViewById(R.id.text);

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


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    Locale locale = new Locale("es_ES");
                    textToSpeech.setLanguage(Locale.forLanguageTag(locale.getLanguage()));
                }
            }
        });

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String getText= s.toString();
                textToSpeech.speak(getText,TextToSpeech.QUEUE_FLUSH,null);
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
}