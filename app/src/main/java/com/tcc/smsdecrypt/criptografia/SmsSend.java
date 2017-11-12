package com.tcc.smsdecrypt.criptografia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tcc.smsdecrypt.LoginActivity;
import com.tcc.smsdecrypt.R;
import com.tcc.smsdecrypt.database.ChavesDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;


public class SmsSend extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGES = 0;
    ImageView sendImgV;
    Button sendBtn;
    EditText txtMessage;
    EditText txtContato;
    String message;
    String contato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_send);

//        getSupportActionBar().setTitle("SMSDecrypt - " + LoginActivity.nomeUsuarioLogado);
        //        getSupportActionBar().setTitle("SMSDecrypt - " + LoginActivity.nomeUsuarioLogado);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        toolbar.setTitle("SMSDecrypt - " + LoginActivity.nomeUsuarioLogado);

        sendImgV = (ImageView) findViewById(R.id.imageSend);
        txtMessage = (EditText) findViewById(R.id.messageText);
        txtContato = (EditText) findViewById(R.id.messageTo);
//        sendBtn = (Button) findViewById(R.id.btnSend);

        txtContato.setText("+5531994429981");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        sendImgV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });

    }

    protected void sendSMSMessage() {

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("myuser", "mypass".toCharArray());
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpURLConnection c = null;

        try {
            ChavesDbHelper db = new ChavesDbHelper(getApplicationContext());
            String tokenAccess = db.getAccessToken(LoginActivity.emailUsuarioLogado);
            System.out.println("tokenaccess: " + tokenAccess);
            if (tokenAccess != null) {
                c = (HttpURLConnection) new URL("http://186.248.79.46:9201/chaves?celular=" + txtContato.getText().toString()).openConnection();
                final String oAuth = "Bearer " + tokenAccess;

                c.setRequestProperty("Authorization", oAuth);

                c.setUseCaches(false);
                System.out.println("PRINTANDO");
                System.out.println(c.getResponseCode());


                BufferedReader in = new BufferedReader(
                        new InputStreamReader(c.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                System.out.println("Response : -- " + response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                System.out.println(jsonObject);
                new EncriptaDecriptaRSA().stringToPublicKey(jsonObject.getJSONArray("content").getJSONObject(0).getString("chavePublica"));

                System.out.println("FIM");

                String mensagemCriptografada = "";
                message = txtMessage.getText().toString();
                contato = txtContato.getText().toString();
//                mensagemCriptografada = new EncriptaDecriptaRSA().criptografaPub(message);

                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(mensagemCriptografada);
                sms.sendMultipartTextMessage(contato, null, parts, null, null);

        /*System.out.println(mensagemCriptografada);
        smsManager.sendTextMessage("+5531994429981", null, a, null, null);*/

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_MESSAGES);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Usuário não encontrado.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_MESSAGES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}
