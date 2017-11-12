package com.tcc.smsdecrypt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tcc.smsdecrypt.requisicoes.CallAPI;

import java.net.HttpURLConnection;


public class ForgotPasswordActivity extends AppCompatActivity {

    EditText _emailText;

    private HttpURLConnection c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        _emailText = (EditText) findViewById(R.id.input_email);

        Button button = (Button) findViewById(R.id.btn_sendVerfCod);

        TextView _loginLink = (TextView) findViewById(R.id.link_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
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


    public boolean sendVerificationCode() {
        if (validade()) {
            /*try {
                c = (HttpURLConnection) new URL("http://186.248.79.46:9201/recuperarSenha").openConnection();

                c.setRequestMethod("POST");

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                String email = _emailText.getText().toString();
                System.out.println(email);

                String json =
                        "{" +
                            "\"email\"" + ":\"" + email+ "\"" +
                        "}";


                OutputStream out = new BufferedOutputStream(c.getOutputStream());

                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

                writer.write(email);

                writer.flush();

                writer.close();

                out.close();

                c.connect();

                System.out.println("Response Code: " + c.getResponseCode());

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

                Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_SHORT).show();


                if (c.getResponseCode() == 200) {

                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }*/
            String email = _emailText.getText().toString();
            CallAPI c = new CallAPI(this, email);
            c.execute();
        }

        return false;
    }

    public boolean validade() {
        String email = _emailText.getText().toString();
        boolean valid = false;
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
            valid = true;
        }

        return valid;
    }
}
