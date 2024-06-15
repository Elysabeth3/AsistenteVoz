package com.elisabeth.asistentevoz;

import android.Manifest;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GestorComandos extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_ALL = 100;

    private String phoneNumberToCall;
    private String comando;

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_ALL);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("comando") != null){
            comando = bundle.getString("comando");
            ejecutarComando();
        }
    }

    public void ejecutarComando(){
        String[] detalles = comando.split(" ");

        String comandoPrincipal = detalles[0].toUpperCase();
        String comandoResto;

        StringBuilder concatenador = new StringBuilder();

        for (int i = 1; i < detalles.length; i++) {
            concatenador.append(detalles[i] + " ");
        }

        comandoResto = concatenador.toString();

        Log.i("INFO", "COMANDO : " + comandoPrincipal);
        Log.i("INFO", "RESTO : " + comandoResto);

        switch (comandoPrincipal){
            case "CÁMARA","FOTO"->{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
            case "LLAMA","LLAMAR"->{
                comandoResto = comandoResto.replace(" ","");
                fetchContactPhoneNumber(comandoResto);
            }
            case "BUSCAR"->{
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, comandoResto);
                startActivity(intent);
            }
            case "ENVIAR"->{
                String[] datos = comandoResto.split(" ");
                String nombreContacto = datos[0];
                String smsNumber = getPhoneNumberByName(nombreContacto);
                smsNumber = "34"+smsNumber;
                String datosResto;
                StringBuilder concatenadorDatos = new StringBuilder();

                for (int i = 1; i < datos.length; i++) {
                    concatenadorDatos.append(datos[i] + " ");
                }
                datosResto = concatenadorDatos.toString();

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, datosResto);
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
                sendIntent.setPackage("com.whatsapp");

                startActivity(sendIntent);
            }
            default->{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private String getPhoneNumberByName(String name) {
        String phoneNumber = null;
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (numberIndex != -1) {
                    phoneNumber = cursor.getString(numberIndex);
                    phoneNumber = phoneNumber.replace(" ", "");
                    if (phoneNumber.startsWith("+")) {
                        phoneNumber = phoneNumber.substring(1);
                    }
                }
            } else {
                Log.d("Contact Info", "No contacts found for name: " + name);
            }
        } catch (Exception e) {
            Log.e("Contact Info", "Error querying contacts: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return phoneNumber;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Ambos permisos han sido otorgados
                fetchContactPhoneNumber("John Doe");
            } else {
                // Permiso denegado, muestra un mensaje al usuario
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchContactPhoneNumber(String contactName) {
        String phoneNumber = getPhoneNumberByName(contactName);
        if (phoneNumber != null) {
            Log.d("Contact Info", "Phone number of " + contactName + ": " + phoneNumber);
            phoneNumberToCall = phoneNumber;
            callPhoneNumber();
        } else {
            Log.d("Contact Info", "Contact not found.");
        }
    }

    private void callPhoneNumber() {
        if (phoneNumberToCall != null && !phoneNumberToCall.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumberToCall));
            try {
                startActivity(intent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Phone number not found", Toast.LENGTH_SHORT).show();
        }
    }
}
