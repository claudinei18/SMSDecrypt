package com.tcc.smsdecrypt.NavigationFragment;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tcc.smsdecrypt.LoginActivity;
import com.tcc.smsdecrypt.R;
import com.tcc.smsdecrypt.requisicoes.CallAPIUpdateRegister;
import com.tcc.smsdecrypt.requisicoes.UserRegistrate;
import com.tcc.smsdecrypt.util.Mask;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by claudinei on 01/11/17.
 */

public class EditProfileFragment extends BaseNavigationFragment {


    EditText _nameText;
    EditText _addressTextLogradouro;
    EditText _addressTextNumero;
    EditText _addressTextComplemento;
    EditText _addressTextBairro;
    EditText _addressTextCEP;
    EditText _addressTextCidade;
    EditText _addressTextEstado;
    EditText _emailText;
    EditText _mobileText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;
    Button _updateProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareFloatingActionButton();

        Resources res = getResources();
        String[] ufs = res.getStringArray(R.array.ufs);

        _addressTextCEP = (EditText) getActivity().findViewById(R.id.input_address_cep);
        _addressTextCEP.addTextChangedListener(Mask.insert(Mask.CEP_MASK, _addressTextCEP));

        _mobileText = (EditText) getActivity().findViewById(R.id.input_mobile);
        _mobileText.addTextChangedListener(Mask.insert(Mask.CELULAR_MASK, _mobileText));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, ufs);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                getActivity().findViewById(R.id.input_address_estado);
        textView.setAdapter(adapter);

        _nameText = (EditText) getActivity().findViewById(R.id.input_name);
        _nameText.setText(LoginActivity.nomeUsuarioLogado);

        _addressTextLogradouro = (EditText) getActivity().findViewById(R.id.input_address_logradouro);
        _addressTextLogradouro.setText(LoginActivity.logradouro);
        _addressTextNumero = (EditText) getActivity().findViewById(R.id.input_address_numero);
        _addressTextNumero.setText(LoginActivity.numero);

        _addressTextComplemento = (EditText) getActivity().findViewById(R.id.input_address_complemento);
        _addressTextComplemento.setText(LoginActivity.complemento);

        _addressTextBairro = (EditText) getActivity().findViewById(R.id.input_address_bairro);
        _addressTextBairro.setText(LoginActivity.bairro);

        _addressTextCEP.setText(LoginActivity.cep);

        _addressTextCidade = (EditText) getActivity().findViewById(R.id.input_address_cidade);
        _addressTextCidade.setText(LoginActivity.cidade);

        _addressTextEstado = (EditText) getActivity().findViewById(R.id.input_address_estado);
        _addressTextEstado.setText(LoginActivity.estado);

        _emailText = (EditText) getActivity().findViewById(R.id.input_email);
        _emailText.setText(LoginActivity.emailUsuarioLogado);

        _mobileText.setText(LoginActivity.celularUsuarioLogado);

        _updateProfile = (Button) getActivity().findViewById(R.id.btn_uptProfile);
        _updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    _updateProfile.setEnabled(false);

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Creating Account...");
                    progressDialog.show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 2500);

                    String name = _nameText.getText().toString();
                    String logradouro = _addressTextLogradouro.getText().toString();
                    String numero = _addressTextNumero.getText().toString();
                    String complemento = _addressTextComplemento.getText().toString();
                    String bairro = _addressTextBairro.getText().toString();
                    String cep = _addressTextCEP.getText().toString();
                    String cidade = _addressTextCidade.getText().toString();
                    String estado = _addressTextEstado.getText().toString();
                    String email = _emailText.getText().toString();
                    String mobile = _mobileText.getText().toString();
                    String password = _passwordText.getText().toString();
                    String reEnterPassword = _reEnterPasswordText.getText().toString();

                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    UserRegistrate user = new UserRegistrate(LoginActivity.codigoPessoaUsuarioLogado, name, email, mobile, encoder.encode(password), logradouro, numero, complemento, bairro, cep, cidade, estado, true);

                    CallAPIUpdateRegister c = new CallAPIUpdateRegister(getActivity(), user);
                    c.execute();


                }
            }
        });


    }

    @Override
    protected void prepareFloatingActionButton() {

    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressTextLogradouro.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();

        _passwordText = (EditText) getActivity().findViewById(R.id.input_password);
        String password = _passwordText.getText().toString();

        _reEnterPasswordText = (EditText) getActivity().findViewById(R.id.input_reEnterPassword);
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

        if (mobile.isEmpty() || mobile.length() < 16) {
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
}