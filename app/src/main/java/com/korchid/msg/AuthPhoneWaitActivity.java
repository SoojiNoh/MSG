package com.korchid.msg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthPhoneWaitActivity extends AppCompatActivity {
    private static final String TAG = "AuthPhoneWaitActivity";
    Button btn_auth;
    Button btn_back;
    EditText et_authCode;

    String token = "dNb4wEIZKv8:APA91bGwcxOlb-_kLZCbNzR6GVxTh9CW7Hb8mnTmo1iBp_Vfr0UEWe3ZsLL6vv02bMMLpi27hL6A57dCRJaFG5Cy4k-kc6QN8ecoT5Uf8V4jzT6J5qkBdZ8ZQoC4O-WgJt566NL-5AnE";
    final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    SMSReceiver smsReceiver;
    String smsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone_wait);

        final String phoneNumber = getIntent().getExtras().getString("phoneNumber");
        final String password = getIntent().getExtras().getString("password");
        smsMessage = "";

        Log.d(TAG, "phoneNumber : " + phoneNumber);
        Log.d(TAG, "password : " + password);

        btn_back = (Button) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_auth = (Button) findViewById(R.id.btn_confirm);
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO check
                GlobalApplication.getGlobalApplicationContext().setUserId(phoneNumber);
                GlobalApplication.getGlobalApplicationContext().setUserPassword(password);

                // http://mommoo.tistory.com/38
                // Use Environmental variable 'SharedPreference'
                SharedPreferences sharedPreferences = getSharedPreferences("login", 0);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("USER_LOGIN", "LOGIN");
                editor.putString("USER_PHONE", phoneNumber);
                editor.putString("USER_PASSWORD", password);
                editor.commit(); // Apply file

                /*
                // Delete preference value
                // 1. Remove "key" data
                editor.remove("key");

                // 2. Remove xml data
                editor.clear();
                */

                // if sharedPreferences.getString value is 0, assign 2th parameter
                Log.d(TAG, "SharedPreference");
                Log.d(TAG, "USER_LOGIN : " + sharedPreferences.getString("USER_LOGIN", "LOGOUT"));
                Log.d(TAG, "USER_PHONE : " + sharedPreferences.getString("USER_PHONE", "000-0000-0000"));
                Log.d(TAG, "USER_PASSWORD : " + sharedPreferences.getString("USER_PASSWORD", "123123"));

                finish();
            }
        });

        et_authCode = (EditText) findViewById(R.id.et_authCode);

        smsReceiver = new SMSReceiver();
        IntentFilter intentFilter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(smsReceiver, intentFilter);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;

                URL url = null;
                String response = null;

                try {
                    url = new URL("https://www.korchid.com/sms-sender/" + phoneNumber + "/" + password + "/" + token);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    String line = "";

                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    isr.close();
                    reader.close();


                } catch (IOException e) {
                    // Error
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( smsReceiver != null ) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    public class SMSReceiver  extends BroadcastReceiver {
        /**
         * 로깅을 위한 태그
         */
        public static final String TAG = "SMSBroadcastReceiver";

        /**
         * 시간 포맷을 위한 형식
         */
        public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() 메소드 호출됨.");

            // SMS 수신 시의 메시지인지 다시 한번 확인합니다.
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Log.i(TAG, "SMS를 수신하였습니다.");

                // SMS 메시지를 파싱합니다.
                Bundle bundle = intent.getExtras();
                Object[] objs = (Object[])bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[objs.length];

                int smsCount = objs.length;
                for(int i = 0; i < smsCount; i++) {
                    // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // API 23 이상
                        String format = bundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
                    }
                }

                // SMS 수신 시간 확인
                Date receivedDate = new Date(messages[0].getTimestampMillis());
                Log.i(TAG, "SMS received date : " + receivedDate.toString());

                // SMS 발신 번호 확인
                String sender = messages[0].getOriginatingAddress();
                Log.i(TAG, "SMS sender : " + sender);

                // SMS 메시지 확인
                String contents = messages[0].getMessageBody().toString();
                Log.i(TAG, "SMS contents : " + contents);
                smsMessage = contents;

                et_authCode.setText(smsMessage);

            }

        }
    }
}
