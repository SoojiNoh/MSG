package com.korchid.msg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;

public class ReserveActivity extends AppCompatActivity {
    Switch sw_enable;

    NumberPicker np_week;
    NumberPicker np_number;

    Button btn_back;
    Button btn_polite;
    Button btn_impolite;
    Button btn_inPerson;

    public static final int TOTAL_WEEK = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        btn_back = (Button) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_polite = (Button) findViewById(R.id.btn_polite);
        btn_impolite = (Button) findViewById(R.id.btn_impolite);
        btn_inPerson = (Button) findViewById(R.id.btn_inPerson);


        sw_enable = (Switch) findViewById(R.id.sw_enable);

        np_week = (NumberPicker) findViewById(R.id.np_week);
        np_number = (NumberPicker) findViewById(R.id.np_number);

        np_week.setMinValue(1);
        np_week.setMaxValue(2);
        np_week.setWrapSelectorWheel(false);

        np_number.setMinValue(1);
        np_number.setMaxValue(7);
        np_number.setWrapSelectorWheel(false);

        np_week.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                int weekNum = view.getValue();

                np_number.setMaxValue(TOTAL_WEEK * weekNum);
            }
        });



    }
}
