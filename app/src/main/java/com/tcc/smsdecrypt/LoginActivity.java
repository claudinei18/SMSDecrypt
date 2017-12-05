package com.tcc.smsdecrypt;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tcc.smsdecrypt.database.ChavesDbHelper;
import com.tcc.smsdecrypt.database.Token;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private boolean notLogged = true;

    private HttpURLConnection c = null;
    ProgressDialog progressDialog = null;

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    public static String celularUsuarioLogado = "";
    public static String codigoPessoa = "";
    public static String nomeUsuarioLogado = "";
    public static String logradouro = "";
    public static String numero = "";
    public static String complemento = "";
    public static String bairro = "";
    public static String cep = "";
    public static String cidade = "";
    public static String estado = "";
    public static String emailUsuarioLogado = "";


    public static String codigoPessoaUsuarioLogado = "";

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    @Bind(R.id.link_forgotPasswd)
    TextView _forgotPasswd;
    @Bind(R.id.link_verifyCode)
    TextView _verifyCode;

    CheckBox cb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        cb = (CheckBox)findViewById(R.id.saveEmail);

        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);

        String emailRecuperado = sp1.getString("email", null);

        _emailText.setText(emailRecuperado);
        if(emailRecuperado != null){
            if(!emailRecuperado.equals("")){
                cb.setChecked(true);
            }
        }


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _forgotPasswd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _verifyCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SendVerificationCode.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _passwordText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(LoginActivity.this, "Autenticando ...", Toast.LENGTH_SHORT).show();
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    private void loginServidor() {
        try {
            c = (HttpURLConnection) new URL("http://186.248.79.46:9201/oauth/token?cliente=angular&username=" + _emailText.getText().toString() +
                    "&password=" + _passwordText.getText().toString() + "&grant_type=password").openConnection();
            final String oauth = "Basic " + Base64.encodeToString("angular:@ngul@r0".getBytes(), Base64.NO_WRAP);

            c.setRequestMethod("POST");
            c.setRequestProperty("Authorization", oauth);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            System.out.println("Response Code: " + c.getResponseCode());

            System.out.println("PRINTANDO");
            System.out.println(c.getResponseCode());

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                checkForPermissionReadState();
            }else{
                continueLogin();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void continueLogin(){
        try{
            System.out.println(c.getResponseCode());
            if (c.getResponseCode() == 200) {
                System.out.println(c.getResponseCode());

                if(cb.isChecked()){
                    SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed=sp.edit();
                    Ed.putString("email", _emailText.getText().toString() );
                    Ed.commit();
                }else{
                    SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed=sp.edit();
                    Ed.putString("email", "");
                    Ed.commit();
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(c.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObject = new JSONObject(response.toString());

                System.out.println(jsonObject.toString());

                System.out.println(jsonObject);

                            /*
                            * Se o IMEI for correto
                            */
                /*TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                System.out.println(telephonyManager.getDeviceId());*/

                Token t = new Token(_emailText.getText().toString(), jsonObject.getString("access_token"), getRefreshToken());
                ChavesDbHelper db = new ChavesDbHelper(getApplicationContext());
                db.insert(t.getEmail(), t.getToken(), t.getRefreshToken());

                System.out.println("SUCESSO1");

                celularUsuarioLogado = jsonObject.getJSONObject("pessoa").getString("celular");
                codigoPessoaUsuarioLogado = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getString("codigo");
                nomeUsuarioLogado = jsonObject.getString("nome");
                logradouro = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("logradouro");
                numero = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("numero");
                complemento = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("complemento");
                bairro = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("bairro");
                cep = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("cep");
                cidade = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("cidade");
                estado = jsonObject.getJSONObject("pessoa").getJSONObject("pessoa").getJSONObject("endereco").getString("estado");
                emailUsuarioLogado = _emailText.getText().toString();

                List<Token> list = db.getChaves();
                for(Token tt: list){
                    System.out.println(tt.getEmail() + " " + tt.getToken());
                }
                System.out.println("SUCESSO2");
                onLoginSuccess();

            } else {
                onLoginFailed();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();

                    continueLogin();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    public void checkForPermissionReadState(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        loginServidor();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
                        if(notLogged)
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 5000);
    }

        public String getRefreshToken() {
        HttpURLConnection conn = c;
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        Set<String> headerFieldsSet = headerFields.keySet();
        Iterator<String> hearerFieldsIter = headerFieldsSet.iterator();

        while (hearerFieldsIter.hasNext()) {

            String headerFieldKey = hearerFieldsIter.next();

            if ("Set-Cookie".equalsIgnoreCase(headerFieldKey)) {

                List<String> headerFieldValue = headerFields.get(headerFieldKey);

                for (String headerValue : headerFieldValue) {

                    System.out.println("Cookie Found...");

                    String[] fields = headerValue.split(";\\s*");

                    String cookieValue = fields[0];
                    String expires = null;
                    String path = null;
                    String domain = null;
                    boolean secure = false;

                    // Parse each field
                    for (int j = 1; j < fields.length; j++) {
                        if ("secure".equalsIgnoreCase(fields[j])) {
                            secure = true;
                        } else if (fields[j].indexOf('=') > 0) {
                            String[] f = fields[j].split("=");
                            if ("expires".equalsIgnoreCase(f[0])) {
                                expires = f[1];
                            } else if ("domain".equalsIgnoreCase(f[0])) {
                                domain = f[1];
                            } else if ("path".equalsIgnoreCase(f[0])) {
                                path = f[1];
                            }
                        }

                    }

                    System.out.println("cookieValue:" + cookieValue);

                    System.out.println("expires:" + expires);
                    System.out.println("path:" + path);
                    System.out.println("domain:" + domain);
                    System.out.println("secure:" + secure);

                    System.out.println("*****************************************");
                    String refreshToken = cookieValue.split("refreshToken")[1];
                    return refreshToken;


                }

            }

        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
        notLogged = false;
//        finish();
//        Intent intent = new Intent(this, MainActivity.class);

        System.out.println("CHAMA A INTENT");
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (c != null) {
            c.disconnect();
        }
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
