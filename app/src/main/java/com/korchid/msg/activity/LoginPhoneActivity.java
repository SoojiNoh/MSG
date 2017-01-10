package com.korchid.msg.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.korchid.msg.adapter.RestfulAdapter;
import com.korchid.msg.retrofit.User;
import com.korchid.msg.retrofit.UserAuth;
import com.korchid.msg.ui.CustomActionbar;
import com.korchid.msg.http.HttpPost;
import com.korchid.msg.R;
import com.korchid.msg.ui.StatusBar;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.korchid.msg.global.QuickstartPreferences.SHARED_PREF_USER_LOGIN;
import static com.korchid.msg.global.QuickstartPreferences.USER_ID_NUMBER;
import static com.korchid.msg.global.QuickstartPreferences.USER_LOGIN_STATE;
import static com.korchid.msg.global.QuickstartPreferences.USER_LOGIN_TOKEN;
import static com.korchid.msg.global.QuickstartPreferences.USER_PASSWORD;
import static com.korchid.msg.global.QuickstartPreferences.USER_PHONE_NUMBER;

public class LoginPhoneActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "LoginPhoneActivity";

    private Button btn_login;
    private Button btn_findPassword;

    private EditText et_phoneNumber;
    private EditText et_password;

    private Spinner sp_nationCode;

    private String internationalPhoneNumber;
    private String phoneNumber;
    private String userPhoneNumber;
    private String nationCode = "";
    private String password;
    private int viewId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        initView();

        final String state = getIntent().getStringExtra(USER_LOGIN_STATE);

        Log.d(TAG, "Login : " + state);

        if (state.equals("LOGIN")) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_LOGIN, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.clear();
            editor.commit();

            Intent intent = new Intent();
            intent.putExtra(USER_LOGIN_STATE, "LOGOUT");
            setResult(RESULT_OK, intent);
            finish();
        }

        et_phoneNumber.setText(userPhoneNumber);


    }

    private void initView(){
        StatusBar statusBar = new StatusBar(this);

        CustomActionbar customActionbar = new CustomActionbar(this, R.layout.actionbar_content, "Login");

        et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        et_password = (EditText) findViewById(R.id.et_password);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_findPassword = (Button) findViewById(R.id.btn_findPassword);

        // Setting nation code spinner
        final String[] option = getResources().getStringArray(R.array.spinnerNationCode);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, option);
        sp_nationCode = (Spinner) findViewById(R.id.sp_nationCode);
        sp_nationCode.setAdapter(arrayAdapter);
        sp_nationCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), option[i], Toast.LENGTH_LONG).show();
                String content = option[i];
                // Delete nation name
                nationCode = content.split(" ")[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_login.setOnClickListener(this);
        btn_login.setEnabled(false);
        btn_findPassword.setEnabled(false);

        // http://egloos.zum.com/killins/v/3008925
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                String input2 = et_phoneNumber.getText().toString();

                if(input.length() > 0 && input2.length() > 0){
                    btn_login.setEnabled(true);
                    btn_login.setBackgroundResource(R.color.colorPrimary);
                    btn_findPassword.setEnabled(true);
                    btn_findPassword.setBackgroundResource(R.color.colorPrimary);
                }else{
                    btn_login.setEnabled(false);
                    btn_findPassword.setEnabled(false);
                }
            }
        };
        et_password.addTextChangedListener(textWatcher);

        btn_findPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        viewId = v.getId();



        switch (viewId){
            case R.id.btn_login:{
                phoneNumber = et_phoneNumber.getText().toString();
                password = et_password.getText().toString();


                internationalPhoneNumber = nationCode + phoneNumber.substring(1); // Remove phoneNumber idx 0

                //Toast.makeText(getApplicationContext(), internationalPhoneNumber, Toast.LENGTH_SHORT).show();

                Call<List<User>> userCall = RestfulAdapter.getInstance().userLogin(internationalPhoneNumber, password);

                userCall.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        User user = response.body().get(0);

                        if(user == null){
                            Toast.makeText(getApplicationContext(), "Please check your id and password", Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_LOGIN, 0);

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(USER_LOGIN_STATE, "LOGIN");
                            editor.putString(USER_PHONE_NUMBER, internationalPhoneNumber);
                            editor.putString(USER_PASSWORD, password);
                            editor.putInt(USER_ID_NUMBER, user.getUser_id());
                            editor.putString(USER_LOGIN_TOKEN, user.getLogin_token_ln());
                            editor.commit(); // Apply file

                            Intent intent = new Intent();
                            intent.putExtra(USER_LOGIN_STATE, "LOGIN");
                            intent.putExtra(USER_PHONE_NUMBER, internationalPhoneNumber);
                            setResult(RESULT_OK, intent);
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.d(TAG, "onFailure");
                    }
                });

                break;
            }
            case R.id.btn_findPassword:{
                phoneNumber = et_phoneNumber.getText().toString();

                if(phoneNumber.equals("")){
                    Toast.makeText(getApplicationContext(), "Please input phone number!", Toast.LENGTH_LONG).show();
                }else{
                    internationalPhoneNumber = nationCode + phoneNumber.substring(1); // Remove phoneNumber idx 0

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginPhoneActivity.this);

                    builder.setTitle("Confirm");
                    builder.setMessage("Please check your phone number.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                            intent.putExtra(USER_PHONE_NUMBER, internationalPhoneNumber);
                            Log.d(TAG, "internationalPhoneNumber : " + internationalPhoneNumber);
                            startActivityForResult(intent, 0);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }


                break;
            }
            default:{
                break;
            }
        }
    }// onClick End


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ActionBar 메뉴 클릭에 대한 이벤트 처리

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case RESULT_OK:{
                // find password activity
                if(requestCode == 0){
                    Log.d(TAG, "find password OK");
                    password = data.getStringExtra(USER_PASSWORD);
                    this.et_password.setText(password);
                }
                break;
            }
            case RESULT_CANCELED:{
                break;
            }
            default:{
                break;
            }
        }

    }
}
