package com.tcc.smsdecrypt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tcc.smsdecrypt.requisicoes.CallAPISendVerificationCode;
import com.tcc.smsdecrypt.util.GeradorSenha;


public class SendVerificationCode extends AppCompatActivity {

    EditText newPasswordEdtText;

    EditText codigoEditText;

    Button btnUpdatePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_verification_code);

        newPasswordEdtText = (EditText) findViewById(R.id.input_newPassword);

        codigoEditText = (EditText) findViewById(R.id.input_code);

        btnUpdatePass = (Button) findViewById(R.id.btn_updatePass);

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeradorSenha gs = new GeradorSenha();
                String senha = newPasswordEdtText.getText().toString();
                String senhaEncriptada = gs.encode(senha);
                System.out.println(senhaEncriptada);
                String codigo = codigoEditText.getText().toString();
                CallAPISendVerificationCode c = new CallAPISendVerificationCode(getBaseContext(), senhaEncriptada, codigo);
                c.execute();
            }
        });


    }
}
