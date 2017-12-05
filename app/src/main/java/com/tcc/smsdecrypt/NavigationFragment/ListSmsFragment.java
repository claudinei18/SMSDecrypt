package com.tcc.smsdecrypt.NavigationFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.tcc.smsdecrypt.ChatAdapter;
import com.tcc.smsdecrypt.ChatMessage;
import com.tcc.smsdecrypt.LoginActivity;
import com.tcc.smsdecrypt.R;
import com.tcc.smsdecrypt.criptografia.EncriptaDecriptaRSA;
import com.tcc.smsdecrypt.database.ChavesDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by claudinei on 13/10/17.
 */

@SuppressLint("ValidFragment")
public class ListSmsFragment extends BaseNavigationFragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_MESSAGES = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;


    ArrayList<Contact> listContacts;
    ListView lvContacts;

    private ChatAdapter adapter;
    private ListView messagesContainer;

    String phoneNumber;

    final String PREFS_NAME = "MyPrefsFile";

    EditText txtMessage;
    ImageButton imageButton;
    String message;
    String contato;

    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGES = 0;

    public ListSmsFragment(String phoneNumber){
        super();
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_chat, container, false);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            checkForPermissions();
        }

        return myView;
    }

    public void checkForPermissions(){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECEIVE_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
//
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
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

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_MESSAGES);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        System.out.println(settings.getBoolean("my_first_time", true));
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            ChavesDbHelper chavesDbHelper = new ChavesDbHelper(getActivity().getApplicationContext());

            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        messagesContainer = (ListView) getActivity().findViewById(R.id.messagesContainer);

//        getSupportActionBar().setTitle("SMSDecrypt - " + LoginActivity.nomeUsuarioLogado);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
//        getActivity().setSupportActionBar(toolbar);
//        toolbar.setTitle("SMSDecrypt - " + LoginActivity.nomeUsuarioLogado);


        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
                System.out.println(mPhoneNumber);
                new EncriptaDecriptaRSA().deletarChaves(getActivity().getApplicationContext());
            }
        });

        System.out.println(settings.getBoolean("my_first_time", true));
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            ChavesDbHelper chavesDbHelper = new ChavesDbHelper(getActivity().getApplicationContext());

            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        imageButton = (ImageButton) getActivity().findViewById(R.id.chatSendButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    sendSMSMessage();
                }catch (Exception e){
                    Toast.makeText(getActivity().getApplicationContext(), "Verifique se esse usuário já se registrou e gerou sua chave!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        txtMessage = (EditText) getActivity().findViewById(R.id.messageEdit);


        carregarMensagens2();
    }

    public void carregarMensagens2(){

        ArrayList<ChatMessage> arr = new ArrayList<ChatMessage>();

        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";

        StringBuilder smsBuilder = null;

        System.out.println("1");
        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = getActivity().getContentResolver().query(uri, projection, "address='" + phoneNumber + "'", null, "date desc");

            smsBuilder = new StringBuilder();

            if (cur.moveToFirst()) {

                int index_Id = cur.getColumnIndex("_id");
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");


                do {
                    smsBuilder = new StringBuilder();

                    Long id = cur.getLong(index_Id);
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);

                    String b[] = strbody.split("Sent from your Twilio trial account -");
                    if (b.length == 3) {
                        strbody = b[2];
                    }

                    ChatMessage msg = new ChatMessage();
                    msg.setId(id);


                    if(int_Type == 2){
                        msg.setMe(true);
                    }else if(strbody.startsWith("ONLY-TO-SHOW")){
                        msg.setMe(false);
                    }
                    long a = 0;
                    try{
                        String msgg;
                        if(strbody.startsWith("ONLY-TO-SHOW")){
                            msgg = strbody.split("ONLY-TO-SHOW")[1];
                            msgg = new EncriptaDecriptaRSA().decriptaOnlyToShow(msgg);

                        }else{
                            msgg = new EncriptaDecriptaRSA().decriptografaPub(strbody);
                            CharsetDecoder UTF8Decoder =
                                    Charset.forName("UTF8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
                            UTF8Decoder.decode(ByteBuffer.wrap(msgg.getBytes(StandardCharsets.UTF_8)));
                        }
                        a = System.currentTimeMillis();
                        long c = System.currentTimeMillis();
                        System.out.println(c - a);
                        System.out.println("Acima está tempo de descriptografia com sucesso Tamanho trafegada " + strbody.length() + " verdade " + msgg.length() );
                        msg.setMessage(msgg);
                    }catch (Exception e){
                        long c = System.currentTimeMillis();
                        System.out.println(c - a);
                        System.out.println("Acima está tempo de descriptografia sem sucesso");
                        msg.setMessage("Mensagem não descriptografada!");
                    }
                    msg.setDate(DateFormat.getDateTimeInstance().format(longDate));
                    arr.add(msg);

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

        adapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        for(int i=arr.size() - 1; i >= 0; i--) {
            ChatMessage message = arr.get(i);
            displayMessage(message);
        }

//        ListContactFragment.progressDialog.dismiss();

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
            ChavesDbHelper db = new ChavesDbHelper(getActivity().getApplicationContext());
            String tokenAccess = db.getAccessToken(LoginActivity.emailUsuarioLogado);
            System.out.println("tokenaccess: " + tokenAccess);
            if (tokenAccess != null) {
                c = (HttpURLConnection) new URL("http://186.248.79.46:9201/chaves?celular=" + phoneNumber.replaceAll("\\D+","")).openConnection();
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


                System.out.println("FIM");

                String mensagemCriptografada = "";
                message = txtMessage.getText().toString();
                long a = System.currentTimeMillis();

                mensagemCriptografada = new EncriptaDecriptaRSA().criptografaPub(message, new EncriptaDecriptaRSA().stringToPublicKey(jsonObject.getJSONArray("content").getJSONObject(0).getString("chavePublica")));

                String mensagemParaMostrar = "ONLY-TO-SHOW" + new EncriptaDecriptaRSA().encriptaOnlyToShow(message);
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(mensagemParaMostrar);
                sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);


                long b = System.currentTimeMillis();
                System.out.println(b - a);
                System.out.println("Acima está tempo de criptografia");

                System.out.println("SENDING DUAL SIM");

                final ArrayList<Integer> simCardList = new ArrayList<>();
                SubscriptionManager subscriptionManager;
                subscriptionManager = SubscriptionManager.from(getActivity());
                final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                        .getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                    int subscriptionId = subscriptionInfo.getSubscriptionId();
                    simCardList.add(subscriptionId);
                }

                for(Integer i : simCardList){
                    System.out.println("I: " + i);
                }

                sms = SmsManager.getDefault();
                parts = sms.divideMessage(mensagemCriptografada);
                sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);


                txtMessage.setText("");
                txtMessage.clearFocus();

                Toast.makeText(getActivity().getApplicationContext(), "SMS enviada.",
                        Toast.LENGTH_LONG).show();


        /*System.out.println(mensagemCriptografada);
        smsManager.sendTextMessage("+5531994429981", null, a, null, null);*/

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_MESSAGES);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), "Usuário não encontrado.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    public boolean sendSMSMessageDualSim(Context ctx, int simID, String toNum, String centerNum, ArrayList<String> smsTextlist, ArrayList<PendingIntent> sentIntentList, ArrayList<PendingIntent> deliveryIntentList) {


        String name;
        try {
            if (simID == 0) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            } else {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            }
            return true;
        } catch (ClassNotFoundException e) {
            Log.e("apipas", "ClassNotFoundException:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("apipas", "NoSuchMethodException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("apipas", "InvocationTargetException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("apipas", "IllegalAccessException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("apipas", "Exception:" + e.getMessage());
        }
        return false;
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    @Override
    protected void prepareFloatingActionButton() {

    }
}
