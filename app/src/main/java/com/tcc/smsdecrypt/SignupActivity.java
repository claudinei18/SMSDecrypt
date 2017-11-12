package com.tcc.smsdecrypt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tcc.smsdecrypt.requisicoes.CallAPIRegistration;
import com.tcc.smsdecrypt.requisicoes.UserRegistrate;
import com.tcc.smsdecrypt.util.Mask;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_address_logradouro)
    EditText _addressTextLogradouro;
    @Bind(R.id.input_address_numero)
    EditText _addressTextNumero;
    @Bind(R.id.input_address_complemento)
    EditText _addressTextComplemento;
    @Bind(R.id.input_address_bairro)
    EditText _addressTextBeirro;
    @Bind(R.id.input_address_cep)
    EditText _addressTextCEP;
    @Bind(R.id.input_address_cidade)
    EditText _addressTextCidade;
    @Bind(R.id.input_address_estado)
    EditText _addressTextEstado;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_mobile)
    EditText _mobileText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Resources res = getResources();
        String[] ufs = res.getStringArray(R.array.ufs);


        _addressTextCEP.addTextChangedListener(Mask.insert(Mask.CEP_MASK, _addressTextCEP));
        _mobileText.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, _mobileText));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ufs);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.input_address_estado);
        textView.setAdapter(adapter);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String logradouro = _addressTextLogradouro.getText().toString();
        String numero = _addressTextNumero.getText().toString();
        String complemento = _addressTextComplemento.getText().toString();
        String bairro = _addressTextBeirro.getText().toString();
        String cep = _addressTextCEP.getText().toString();
        String cidade = _addressTextCidade.getText().toString();
        String estado = _addressTextEstado.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        enviarDadosUsuarioServidor();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(getBaseContext(), "Signup success", Toast.LENGTH_LONG).show();
        finish();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressTextLogradouro.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressTextLogradouro.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressTextLogradouro.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()<16) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public boolean enviarDadosUsuarioServidor(){
        String name = _nameText.getText().toString();
        String logradouro = _addressTextLogradouro.getText().toString();
        String numero = _addressTextNumero.getText().toString();
        String complemento = _addressTextComplemento.getText().toString();
        String bairro = _addressTextBeirro.getText().toString();
        String cep = _addressTextCEP.getText().toString();
        String cidade = _addressTextCidade.getText().toString();
        String estado = _addressTextEstado.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserRegistrate user = new UserRegistrate("", name, email, mobile, encoder.encode(password), logradouro, numero, complemento, bairro, cep, cidade, estado, true);

        CallAPIRegistration c = new CallAPIRegistration(getBaseContext(), user);


        if (c.execute().equals("true")) {
            onSignupSuccess();
            return true;
        } else {
            onSignupFailed();
            return false;
        }

        /*try{
            HttpURLConnection c = null;

            c = (HttpURLConnection) new URL("http://186.248.79.46:9201/pessoas").openConnection();

            c.setRequestMethod("POST");
            c.setConnectTimeout(5000);
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            c.setDoInput(true);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }



            System.out.println("ENVIANDO");
            System.out.println("FIM");
            String json =
                "{" +
                    "\"nome\"" + ":\"" + name + "\"," +
                    "\"email\"" + ":\"" + email + "\"," +
                    "\"celular\"" + ":\"" + mobile + "\"," +
                    "\"password\"" + ":\"" + encoder.encode(password) + "\"," +
                    "\"endereco\"" + ": " + "{ " +
                        "\"logradouro\"" + ":\"" +  logradouro + "\"," +
                        "\"numero\"" + ":\"" +  numero + "\"," +
                        "\"complemento\"" + ":\"" +  complemento + "\"," +
                        "\"bairro\"" + ":\"" +  bairro + "\"," +
                        "\"cep\"" + ":\""  + cep + "\"," +
                        "\"cidade\"" + ":\"" +  cidade + "\"," +
                        "\"estado\"" + ":\"" +  estado + "\"" +
                    "}," +
                    "\"ativo\"" + ":\"" + "true\"" +
                "}";

            JSONObject jsonObject2 = new JSONObject(json);
            System.out.println(jsonObject2);

            OutputStream os = c.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            System.out.println(c.getResponseCode());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            System.out.println(jsonObject);




        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;*/
    }
}