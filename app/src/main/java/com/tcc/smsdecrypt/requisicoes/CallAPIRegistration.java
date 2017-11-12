package com.tcc.smsdecrypt.requisicoes;

/**
 * Created by claudinei on 31/10/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by claudinei on 08/10/17.
 */

public class CallAPIRegistration extends AsyncTask<String, String, String> {

    Context context;
    String email = "";
    UserRegistrate user;
    public CallAPIRegistration(Context c, UserRegistrate user){
        //set context variables if required
        this.context = c;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        try{
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

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            System.out.println("ENVIANDO");
            System.out.println("FIM");
            String json =
                    "{" +
                            "\"nome\"" + ":\"" + user.getNome() + "\"," +
                            "\"email\"" + ":\"" + user.getEmail() + "\"," +
                            "\"celular\"" + ":\"" + user.getCelular()+ "\"," +
                            "\"password\"" + ":\"" + user.getPassword() + "\"," +
                            "\"endereco\"" + ": " + "{ " +
                            "\"logradouro\"" + ":\"" +  user.getLogradouro() + "\"," +
                            "\"numero\"" + ":\"" +  user.getNumero() + "\"," +
                            "\"complemento\"" + ":\"" +  user.getComplemento() + "\"," +
                            "\"bairro\"" + ":\"" +  user.getBairro() + "\"," +
                            "\"cep\"" + ":\""  + user.getCep() + "\"," +
                            "\"cidade\"" + ":\"" +  user.getCidade() + "\"," +
                            "\"estado\"" + ":\"" +  user.getEstado() + "\"" +
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


            if (c.getResponseCode() == 200) {
                return "true";
            } else {
                return "false";
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "ERRO";
    }
}
