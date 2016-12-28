package com.korchid.msg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.korchid.msg.ui.CustomActionbar;
import com.korchid.msg.http.HttpPost;
import com.korchid.msg.R;
import com.korchid.msg.ui.StatusBar;

import java.util.HashMap;

import static com.korchid.msg.global.QuickstartPreferences.SHARED_PREF_USER_INFO;
import static com.korchid.msg.global.QuickstartPreferences.SHARED_PREF_USER_LOGIN;
import static com.korchid.msg.global.QuickstartPreferences.USER_ID_NUMBER;
import static com.korchid.msg.global.QuickstartPreferences.USER_PROFILE;
import static com.korchid.msg.global.QuickstartPreferences.USER_SEX;

// Setting user information
public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";

    private ImageView iv_profile;

    private EditText et_nickname;

    private RadioGroup rbtnGroup;
    private RadioButton rbtn_male;
    private RadioButton rbtn_female;
    private RadioButton rbtn_etc;


    private Button btn_register;

    private String userId = "";
    private String profile = "/";
    private String sex = "";
    private String nickname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();

    }

    private void initView(){
        StatusBar statusBar = new StatusBar(this);

        CustomActionbar customActionbar = new CustomActionbar(this, R.layout.actionbar_content, "User Information");


        iv_profile = (ImageView) findViewById(R.id.iv_profile);


        et_nickname = (EditText) findViewById(R.id.et_nickname);

        rbtnGroup = (RadioGroup) findViewById(R.id.rbtnGroup);

        rbtn_male = (RadioButton) findViewById(R.id.rbtn_male);
        rbtn_female = (RadioButton) findViewById(R.id.rbtn_female);
        rbtn_etc = (RadioButton) findViewById(R.id.rbtn_etc);

        btn_register = (Button) findViewById(R.id.btn_register);

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        btn_register.setEnabled(false);

        rbtnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(!btn_register.isEnabled()) {
                    btn_register.setBackgroundResource(R.color.colorPrimary);
                    btn_register.setEnabled(true);
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Register user information - web server
                int id = rbtnGroup.getCheckedRadioButtonId();

                switch (id){
                    case R.id.rbtn_male:{
                        Toast.makeText(getApplicationContext(), "Male", Toast.LENGTH_SHORT).show();
                        sex = "Male";
                        break;
                    }
                    case R.id.rbtn_female:{
                        Toast.makeText(getApplicationContext(), "Female", Toast.LENGTH_SHORT).show();
                        sex = "Female";
                        break;
                    }
                    case R.id.rbtn_etc:{
                        Toast.makeText(getApplicationContext(), "Etc", Toast.LENGTH_SHORT).show();
                        sex = "Etc";
                        break;
                    }
                    default:{
                        break;
                    }
                }

                nickname = et_nickname.getText().toString();

                if(nickname.equals(null)){
                    nickname = "Me";
                }

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_LOGIN , 0);

                userId = sharedPreferences.getString(USER_ID_NUMBER, "");

                String stringUrl = "https://www.korchid.com/msg-user-info";
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("profile", profile);
                params.put("sex", sex);
                params.put("nickname", nickname);

                Handler httpHandler = new Handler();

                HttpPost httpPost = new HttpPost(stringUrl, params, httpHandler);
                httpPost.start();

                Intent intent = new Intent(getApplicationContext(), ReserveActivity.class);
                startActivityForResult(intent, 0);

            }
        });

    }

    class httpHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            String response = msg.getData().getString("response");

            String[] line = response.split("\n");

            Toast.makeText(getApplicationContext(), "response : " + response, Toast.LENGTH_LONG).show();

            if(line[0].equals("Error")){
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }else if(line[0].equals("No")){
                Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();



                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_INFO, 0);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(USER_PROFILE, profile);
                editor.putString(USER_SEX, sex);
                editor.commit(); // Apply file

                Log.d(TAG, "SharedPreference");
                Log.d(TAG, "USER_PROFILE : " + sharedPreferences.getString(USER_PROFILE, ""));
                Log.d(TAG, "USER_SEX : " + sharedPreferences.getString(USER_SEX, "Etc"));

                Intent intent = new Intent();
                //intent.putExtra("result_msg", "결과가 넘어간다 얍!");
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_next, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ActionBar 메뉴 클릭에 대한 이벤트 처리

        int id = item.getItemId();

        switch (id){
            case android.R.id.home: {
                this.finish();
                break;
            }
            case R.id.itemNext:{
                if(btn_register.isEnabled()){
                    int btn_id = rbtnGroup.getCheckedRadioButtonId();

                    Intent intent = new Intent(getApplicationContext(), ReserveActivity.class);

                    switch (btn_id){
                        case R.id.rbtn_male:{
                            Toast.makeText(getApplicationContext(), "Male", Toast.LENGTH_SHORT).show();

                            intent.putExtra("Sex", "Male");
                            break;
                        }
                        case R.id.rbtn_female:{
                            Toast.makeText(getApplicationContext(), "Female", Toast.LENGTH_SHORT).show();

                            intent.putExtra("Sex", "Female");
                            break;
                        }
                        case R.id.rbtn_etc:{
                            Toast.makeText(getApplicationContext(), "Etc", Toast.LENGTH_SHORT).show();

                            intent.putExtra("Sex", "Etc");
                            break;
                        }
                        default:{
                            break;
                        }
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_LOGIN, 0);

                    userId = sharedPreferences.getString(USER_ID_NUMBER, "");

                    String stringUrl = "https://www.korchid.com/msg-user-info";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("userId", userId);
                    params.put("profile", profile);
                    params.put("sex", sex);

                    Handler httpHandler = new Handler();

                    HttpPost httpPost = new HttpPost(stringUrl, params, httpHandler);
                    httpPost.start();

                    startActivityForResult(intent, 0);
                }else{
                    Toast.makeText(this.getApplicationContext(), "Check radio button", Toast.LENGTH_SHORT).show();
                }
                //

                break;
            }
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case RESULT_OK:{
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            default:{
                break;
            }
        }

    }
}

