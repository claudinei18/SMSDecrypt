package com.tcc.smsdecrypt.requisicoes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by claudinei on 08/10/17.
 */

public class CallAPISendVerificationCode extends AsyncTask<String, String, String> {

    Context context;
    String senha = "";
    String codigo = "";

    public CallAPISendVerificationCode(Context c, String senha, String codigo){
        //set context variables if required
        this.context = c;
        this.senha = senha;
        this.codigo = codigo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        OutputStream out = null;
        try {

            try {
                Handler handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context, "Atualizando nova senha ...", Toast.LENGTH_LONG).show();
                    }
                });
                HttpURLConnection c = (HttpURLConnection) new URL("http://186.248.79.46:9201/recuperarSenha/" + codigo).openConnection();

                c.setRequestMethod("POST");

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                String json =
                        "{" +
                            "\"codRecup\"" + ":\"" + this.codigo+ "\"" + "," +
                            "\"novaSenha\"" + ":\"" + this.senha+ "\"" +
                        "}";


                out = new BufferedOutputStream(c.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(this.senha);

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

                if (c.getResponseCode() == 200) {
                    handler =  new Handler(context.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(context, "E-mail enviado! Favor olhar sua caixa de mensagens! ", Toast.LENGTH_LONG).show();
                        }
                    });
                    return ""+true;
                } else {
                    handler =  new Handler(context.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(context, "E-mail não enviado! Favor verifique se o e-mail está correto e tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return ""+false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ""+false;
            }


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }

        return "";
    }
}
