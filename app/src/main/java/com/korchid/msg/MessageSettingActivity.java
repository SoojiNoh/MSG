package com.korchid.msg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by mac0314 on 2016-11-30.
 */

public class MessageSettingActivity extends AppCompatActivity {
    String nickname;
    String title;

    private ArrayList<Setting> m_arry;
    private MessageSettingAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent data = getIntent();
        nickname = "aa";
        title = "aa";
        setList();
    }

    private void setList(){

        m_arry = new ArrayList<Setting>();

        ListView listV = (ListView)findViewById(R.id.listView2);

        m_arry.add(new Setting("잘지내시죠?","@drawable/image", nickname, 0, title));

        m_arry.add(new Setting("별일 없으신가요?","@drawable/image", nickname, 2, title));

        m_arry.add(new Setting("건강은 어떠세요?","@drawable/image", nickname, 3, title));

        m_arry.add(new Setting("식사하셨어요?","@drawable/image", nickname, 4, title));

        adapter = new MessageSettingAdapter(MessageSettingActivity.this, m_arry);

        listV.setAdapter(adapter);

        //lv.setDivider(null); 구분선을 없에고 싶으면 null 값을 set합니다.

        //lv.setDividerHeight(5); 구분선의 굵기를 좀 더 크게 하고싶으면 숫자로 높이 지정

    }

    public void listUpdate(){
        adapter.notifyDataSetChanged(); //​리스트뷰 값들의 변화가 있을때 아이템들을 다시 배치 할 때 사용되는 메소드입니다.
    }
}