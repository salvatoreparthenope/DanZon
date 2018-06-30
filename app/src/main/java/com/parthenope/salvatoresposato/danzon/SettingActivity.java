package com.parthenope.salvatoresposato.danzon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.editTextEmail) EditText editTextEmail;
    @BindView(R.id.editTextPhone) EditText editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initComponent();
    }

    /**
     *
     */
    private void initComponent() {
        String phoneNumbers = Variable.getValue(GlobalConstant.KEY_PHONE_NUMBERS);
        editTextPhone.setText( phoneNumbers == null ? "" : phoneNumbers);
        String email = Variable.getValue(GlobalConstant.KEY_ALERT_EMAIL);
        editTextEmail.setText( email == null ? "" : email);
    }

    @OnClick(R.id.btnSave)
    public void save(){

        if(!PhoneNumberUtils.isGlobalPhoneNumber(editTextPhone.getText().toString()))
        {
            editTextPhone.setError(getString(R.string.text_sett_error));
            return;
        }

        Variable.updateOrAddVariabile(GlobalConstant.KEY_PHONE_NUMBERS,editTextPhone.getText().toString());

        Toast.makeText(SettingActivity.this,getString(R.string.text_sett_save_successfully),Toast.LENGTH_LONG).show();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
