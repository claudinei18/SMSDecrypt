package com.tcc.smsdecrypt.NavigationFragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcc.smsdecrypt.ContactAdapter;
import com.tcc.smsdecrypt.R;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by claudinei on 13/10/17.
 */

public class ListContactFragment extends BaseNavigationFragment {

    ArrayList<Contact> listContacts;
    ListView lvContacts;

    EditText txtMessage;
    ImageButton imageButton;

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
    String selected = "";

    ProgressDialog progressDialog = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myView = inflater.inflate(R.layout.activity_main, container, false);

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme_Dark);

// clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

// inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.list_contact, null, false);

        /*myView = inflater.inflate(R.layout.list_contact, container, false);
        return myView;*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            checkForPermission();
        }else{
            readContacts();
        }



        imageButton = (ImageButton) getActivity().findViewById(R.id.choiceContact);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = ((TextView) getActivity().findViewById(R.id.contactTo)).getText().toString();

                /*Fragment fragment = new ListSmsFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();*/
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new ListSmsFragment(selected));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        txtMessage = (EditText) getActivity().findViewById(R.id.messageEdit);


    }

    public void checkForPermission() {
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
                        MY_PERMISSIONS_REQUEST_READ_SMS);
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

    public void readContacts(){

        Uri uri = Uri.parse("content://sms/");
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, "date DESC");

        HashMap<String, String> hashMap = new HashMap<>();

        listContacts = new ArrayList<Contact>();

        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String phoneNumber = c.getString(c.getColumnIndexOrThrow("address"));
                if(!hashMap.containsKey(phoneNumber)){
                    String date = c.getString(c.getColumnIndexOrThrow("date"));

                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date2 = new Date(Long.parseLong(date));

                    Contact contact = new Contact("" + i, phoneNumber);
                    contact.addNumber(date2.toString(), "celular");
                    listContacts.add(contact);

                    hashMap.put(phoneNumber, "celular");
                }
                c.moveToNext();
            }
        }
        c.close();

        /*int i = 0;
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Contact contact = new Contact("" + i++, key);
            listContacts.add(contact);
        }*/


        lvContacts = (ListView) getActivity().findViewById(R.id.lvContacts);
        ContactAdapter adapterContacts = new ContactAdapter(getActivity().getBaseContext(), listContacts);
        lvContacts.setAdapter(adapterContacts);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String selected = ((TextView) view.findViewById(R.id.tvName)).getText().toString();
                System.out.println(selected);

                /*Fragment fragment = new ListSmsFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();*/

                /*progressDialog = new ProgressDialog(getActivity(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Reading sms from " + selected + "...");
                progressDialog.show();*/

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new ListSmsFragment(selected));
                ft.addToBackStack(null);
                ft.commit();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        System.out.println(requestCode);
        System.out.println(MY_PERMISSIONS_REQUEST_READ_SMS);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    readContacts();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    protected void prepareFloatingActionButton() {

    }
}
