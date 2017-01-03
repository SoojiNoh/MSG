package com.korchid.msg.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.korchid.msg.Chatting;
import com.korchid.msg.adapter.ChattingAdapter;
import com.korchid.msg.http.HttpPost;
import com.korchid.msg.ui.CustomActionbar;
import com.korchid.msg.mqtt.MqttServiceDelegate;
import com.korchid.msg.mqtt.MqttServiceDelegate.MessageHandler;
import com.korchid.msg.mqtt.MqttServiceDelegate.MessageReceiver;
import com.korchid.msg.mqtt.MqttServiceDelegate.StatusHandler;
import com.korchid.msg.mqtt.MqttServiceDelegate.StatusReceiver;
import com.korchid.msg.R;
import com.korchid.msg.ui.StatusBar;
import com.korchid.msg.mqtt.service.MqttService;
import com.korchid.msg.mqtt.service.MqttService.ConnectionStatus;


/**
 * Created by mac0314 on 2016-11-28.
 */

// Chatting between parent and child
public class ChattingActivity extends AppCompatActivity implements View.OnClickListener, MessageHandler, StatusHandler {
    private static final String TAG = "ChattingActivity";

    private Button btn_setting;
    private Button btn_menu;
    private Button btn_plus;
    private Button btn_send;


    private GridLayout expandedMenu;
    private ListView lv_message;

    private EditText et_message;

    private Handler handler = new Handler();

    private MessageReceiver msgReceiver;
    private StatusReceiver statusReceiver;

    private String nickname;
    private String parentName;
    private String count;
    private String title;
    private String message1;
    private int viewId;
    byte[] pic;


    private Boolean expandedState = false;

    private ArrayList<Chatting> m_arr;
    private ChattingAdapter adapter;
    private static String chatMessage = new String();

    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        parentName = intent.getStringExtra("parentName");

        pic = null;
        count = "";
        nickname = "Me";
        title = intent.getStringExtra("topic");
        m_arr = new ArrayList<Chatting>();

        initView();





        Log.d(TAG, "Topic : " + title);

        //Toast.makeText(getApplicationContext(), "Topic : " + title, Toast.LENGTH_LONG).show();


        // Register mqtt topic - Web server
//        String url = "https://www.korchid.com/msg-mqtt";
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("topic", title);
//
//
//        HttpPost httpPost = new HttpPost(url, params, new Handler());
//        httpPost.start();

        //Init Receivers
        bindStatusReceiver();
        bindMessageReceiver();

        //MqttServiceDelegate.topic = title;

        //Start service if not started
        MqttServiceDelegate.startService(this, title);



    }

    private void initView(){
        StatusBar statusBar = new StatusBar(this);

        CustomActionbar customActionbar = new CustomActionbar(this, R.layout.actionbar_content, parentName);

        btn_plus = (Button)findViewById(R.id.btn_plus);


        lv_message = (ListView)findViewById(R.id.lv_message);

        et_message = (EditText)findViewById(R.id.et_message);

        btn_send = (Button)findViewById(R.id.btn_send);

        expandedMenu = (GridLayout) findViewById(R.id.expandedMenu);


        btn_send.setOnClickListener(this);
        btn_plus.setOnClickListener(this);

        btn_send.setEnabled(false);

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

                if(input.length() > 0){
                    btn_send.setEnabled(true);
                    btn_send.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    btn_send.setEnabled(false);
                    btn_send.setTextColor(getResources().getColor(R.color.colorTransparent));
                }
            }
        };

        et_message.addTextChangedListener(textWatcher);

        adapter = new ChattingAdapter(ChattingActivity.this, m_arr);
        lv_message.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        viewId = v.getId();

        switch (viewId){
            case R.id.btn_send:{

                String message = nickname+": "+ et_message.getText().toString();


                //TODO modify topic
                String topic = title;

                MqttServiceDelegate.publish(
                        ChattingActivity.this,
                        topic,
                        message.getBytes()
                );

                et_message.setText("");
                break;
            }
            case R.id.btn_plus:{
                //Toast.makeText(getApplicationContext(), "Plus button", Toast.LENGTH_LONG).show();

                if(expandedState){
                    expandedMenu.bringToFront();
                    expandedMenu.setVisibility(View.GONE);
                    expandedState = false;
                }else{
                    expandedMenu.bringToFront();
                    expandedMenu.setVisibility(View.VISIBLE);
                    expandedState = true;
                }
                break;
            }
            default:{
                break;
            }

        }

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
            case R.id.itemLogo: {

                break;
            }
            case R.id.itemShare:{

                break;
            }
            case R.id.itemSetting: {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                break;
            }
            default: {
                break;
            }
        }

        return true;
    }

    //비트맵의 byte배열을 얻는다
    public byte[] getImageByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        return out.toByteArray();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");

        MqttServiceDelegate.stopService(this);

        unbindMessageReceiver();
        unbindStatusReceiver();

        super.onDestroy();
    }

    private void bindMessageReceiver(){
        Log.d(TAG, "bindMessageReceiver");
        msgReceiver = new MessageReceiver();
        msgReceiver.registerHandler(this);
        registerReceiver(msgReceiver,
                new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT));
    }

    private void unbindMessageReceiver(){
        Log.d(TAG, "unbindMessageReceiver");
        if(msgReceiver != null){
            msgReceiver.unregisterHandler(this);
            unregisterReceiver(msgReceiver);
            msgReceiver = null;
        }
    }

    private void bindStatusReceiver(){
        Log.d(TAG, "bindStatusReceiver");
        statusReceiver = new StatusReceiver();
        statusReceiver.registerHandler(this);
        registerReceiver(statusReceiver,
                new IntentFilter(MqttService.MQTT_STATUS_INTENT));
    }

    private void unbindStatusReceiver(){
        Log.d(TAG, "unbindStatusReceiver");
        if(statusReceiver != null){
            statusReceiver.unregisterHandler(this);
            unregisterReceiver(statusReceiver);
            statusReceiver = null;
        }
    }

    private String getCurrentTimestamp(){
        return new Timestamp(new Date().getTime()).toString();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        //MqttServiceDelegate.topic = title;

        //Start service if not started
        MqttServiceDelegate.startService(this, title);
    }

    @Override
    public void handleMessage(String topic, byte[] payload) {
        Log.d(TAG, "handleMessage");
        String message = new String(payload);
        String name = "";

        String changingString = "";
        String changedString="";
        int start;
        int end;
        changingString = message;

        //TODO modify topic
        //String roomTopic = "/oneM2M/req/"+ title +"/:mobius-yt/xml";
        String roomTopic = title;

        Log.d(TAG, topic);
        Log.d(TAG, message);

        if(!topic.equals(roomTopic)){
            return;
        }

        if(chatMessage != null){

            m_arr.add(new Chatting(nickname, message));


            message="";
            lv_message.setSelection(adapter.getCount()-1);
        }

    }

    public void listUpdate(){

        adapter.notifyDataSetChanged(); //​리스트뷰 값들의 변화가 있을때 아이템들을 다시 배치 할 때 사용되는 메소드입니다.

    }

    @Override
    public void handleStatus(ConnectionStatus status, String reason) {
        Log.d(TAG, "handleStatus: status = " + status + ", reason = " + reason);
    }

}
