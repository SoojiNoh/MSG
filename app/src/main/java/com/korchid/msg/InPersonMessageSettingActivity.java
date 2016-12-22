package com.korchid.msg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

// Setting message that user write in person
public class InPersonMessageSettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "InPersonSettingActivity";

    Button btn_add;
    Button btn_register;

    EditText et_inPersonMessage;


    TextView tv_message1;
    TextView tv_message2;
    TextView tv_message3;
    TextView tv_message4;
    TextView tv_message5;

    int messageNumber = 0;

    int viewId;


    ArrayList<String> message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_person_message_setting);


        StatusBar statusBar = new StatusBar(this);

        CustomActionbar customActionbar = new CustomActionbar(this, R.layout.actionbar_content, "Message setting");


        message = new ArrayList<>();

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);


        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        et_inPersonMessage = (EditText) findViewById(R.id.et_inPersonMessage);

        tv_message1 = (TextView) findViewById(R.id.tv_message1);
        tv_message2 = (TextView) findViewById(R.id.tv_message2);
        tv_message3 = (TextView) findViewById(R.id.tv_message3);
        tv_message4 = (TextView) findViewById(R.id.tv_message4);
        tv_message5 = (TextView) findViewById(R.id.tv_message5);

    }


    @Override
    public void onClick(View view) {
        viewId = view.getId();



        switch (viewId){
            case R.id.btn_add:{
                String reservationMessage = et_inPersonMessage.getText().toString();
                // Clear EditText
                et_inPersonMessage.getText().clear();
                switch (messageNumber){
                    case 0:{
                        tv_message1.setText(reservationMessage);
                        message.add(reservationMessage);
                        break;
                    }
                    case 1:{
                        tv_message2.setText(reservationMessage);
                        message.add(reservationMessage);
                        break;
                    }
                    case 2:{
                        tv_message3.setText(reservationMessage);
                        message.add(reservationMessage);
                        break;
                    }
                    case 3:{
                        tv_message4.setText(reservationMessage);
                        message.add(reservationMessage);
                        break;
                    }
                    case 4:{
                        tv_message5.setText(reservationMessage);
                        message.add(reservationMessage);
                        break;
                    }
                    default:{
                        break;
                    }
                }


                if(messageNumber < 5){
                    messageNumber++;
                }

                break;
            }
            case R.id.btn_register:{


                Intent intent = new Intent();
                String result = "";
                for(int i=0; i<message.size(); i++){
                    result += message.get(i).toString() + "/";
                }
                Log.d(TAG, "result : " + result);

                intent.putExtra("message", result);
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
