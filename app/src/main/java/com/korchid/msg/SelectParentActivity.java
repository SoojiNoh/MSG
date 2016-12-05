package com.korchid.msg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SelectParentActivity extends AppCompatActivity {

    ViewPager pager;
    SeekBar seekBar;
    Button button, button2, button3, button4;
    ImageView imageView;

    // Temp Data Array
    String[] parent = {"Father", "Mother", "StepMother"};
    String[] phoneNum = {"010-0000-0001", "010-0000-0002", "010-0000-0003" };
    String[] topic = {"Sajouiot03", "Sajouiot02", "Sajouiot01"};
    int[] imageId = {R.drawable.tempfa, R.drawable.tempmom, R.drawable.tempstepmom};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_parent);

        pager = (ViewPager) findViewById(R.id.pager);

        SelectParentActivity.MyAdapter adapter = new SelectParentActivity.MyAdapter();
        pager.setAdapter(adapter);

        //imageView = (ImageView) findViewById(R.id.imageView);

        //imageView.setImageResource(imageId[0]);

        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        buttonListener(button2, button3, button4, 0);

        // SeekBar setting
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(pager.getAdapter().getCount() - 1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pager.setCurrentItem(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Pager setting
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                seekBar.setProgress(position);

                //imageView.setImageResource(imageId[position]);
                buttonListener(button2, button3, button4, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Pager Adapter
    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return parent.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);



            TextView textview = new TextView(getApplicationContext());
            textview.setText(parent[position]);
            textview.setTextSize(40.0f);
            layout.addView(textview);

            RoundedImageView circularImageView = new RoundedImageView(getApplicationContext());
            circularImageView.setImageResource(imageId[position]);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId[position]);
//            bitmap = circularImageView.getCroppedBitmap(bitmap, 300);
//            circularImageView.setImageBitmap(bitmap);
            layout.addView(circularImageView);

            //ImageView imageView = new ImageView(getApplicationContext());
            //imageView.setImageResource(imageId[position]);


            //layout.addView(textview);
            //layout.addView(imageView);

            container.addView(layout);

            return layout;
        }
    }

    public void buttonListener(Button b1, Button b2, Button b3, final int idx){
        // Call button
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show dial
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNum[idx]));

                //Direct call
                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum[idx]));
                startActivity(intent);
            }
        });

        // Chatting button
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), topic[position], Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("topic", topic[idx]);
                startActivity(intent);
            }
        });

        // Message Setting button
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessageSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}