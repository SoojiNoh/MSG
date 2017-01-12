package com.korchid.msg.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.korchid.msg.R;
import com.korchid.msg.alarm.AlarmBroadCastReciever;
import com.korchid.msg.alarm.AlarmMatchingBroadCastReceiver;
import com.korchid.msg.alarm.AlarmUtil;
import com.korchid.msg.http.HttpGet;
import com.korchid.msg.ui.StatusBar;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;

import static com.korchid.msg.alarm.AlarmBroadCastReciever.INTENTFILTER_BROADCAST_TIMER;
import static com.korchid.msg.alarm.AlarmBroadCastReciever.KEY_DEFAULT;
import static com.korchid.msg.global.QuickstartPreferences.SHARED_PREF_USER_INFO;
import static com.korchid.msg.global.QuickstartPreferences.USER_NICKNAME;
import static com.korchid.msg.global.QuickstartPreferences.USER_PHONE_NUMBER;
import static com.korchid.msg.global.QuickstartPreferences.USER_ROLE;

public class SelectOpponentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "SelectOpponentActivity";

    private Button btn_call, btn_chat, btn_chat_setting;

    private TextView tv_timeTitle;
    private TextView tv_timeReserved;
    private TextView tv_messageReserved;

    private static RoundedImageView circularImageView;


    private int mPrevPosition;
    private LinearLayout mPageMark;

    // Temp Data Array
    private static ArrayList<String> parentArrayList;

    private static String[] parent = {"Father", "Mother", "StepMother"};
    private static String[] phoneNum = {"010-0000-0001", "010-0000-0002", "010-0000-0003" };
    private static String[] topic = {"Sajouiot03", "Sajouiot02", "Sajouiot01"};
    private static String[] timeReserved = {"수요일 9시경", "목요일 5시경", "금요일 8시경"};
    private static String[] message = {"아빠 뭐해?", "엄마 뭐해?", "엄마 뭐해요?"};
    private static String[] profile = {"https://s3.ap-northeast-2.amazonaws.com/korchid/com.korchid.msg/image/profile/black_rubber_shoes.png"
                                        ,"https://s3.ap-northeast-2.amazonaws.com/korchid/com.korchid.msg/image/profile/her_logo.png"
                                        ,"https://s3.ap-northeast-2.amazonaws.com/korchid/com.korchid.msg/image/profile/her_os_loading"};
    private static int[] imageId = {R.drawable.tempfa, R.drawable.tempmom, R.drawable.tempstepmom};


    private int viewId;
    private int profileWidth;
    private int profileHeight;
    private String userRole = "";
    private static String userPhoneNumber = "";
    private static String userNickname = "";
    private int count;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Remove previous activity
        MainActivity mainActivity = (MainActivity) MainActivity.activity;
        mainActivity.finish();

        userRole = getIntent().getStringExtra(USER_ROLE);
        userPhoneNumber = getIntent().getStringExtra(USER_PHONE_NUMBER);
        parentArrayList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_INFO, 0);

        userNickname = sharedPreferences.getString(USER_NICKNAME, "");

        initView();



/*
        String stringUrl = "https://www.korchid.com/msg-mapping/" + userPhoneNumber;

        Handler httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage");

                String response = msg.getData().getString("response");

                String[] line = response.split("\n");

                Log.d(TAG, "response : " + response);
                //Toast.makeText(getApplicationContext(), "response : " + response, Toast.LENGTH_LONG).show();

                if (line[0].equals("Error")) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();



                } else {
                    String topic = line[0];



                    for(int i=0; i<line.length; i++){
                        String[] dataArray = line[i].split("/");

                        String[] content = dataArray[2].split(":");

                        parentArrayList.add(content[1]);
                        Log.d(TAG, "dataArray : " + dataArray[i]);
                    }

                    Toast.makeText(getApplicationContext(), "Topic : " + topic, Toast.LENGTH_LONG).show();

                }
            }
        };


        HttpGet httpGet = new HttpGet(stringUrl, httpHandler);
        httpGet.start();
  */
    }

    private void initView(){
         /*
        getLayoutInflater().inflate(R.layout.nav_header_main, null);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_myName = (TextView) findViewById(R.id.tv_name);
        tv_myPhoneNumber = (TextView) findViewById(R.id.tv_phoneNumber);

        tv_myName.setText(GlobalApplication.getGlobalApplicationContext().getUserId());
        tv_myPhoneNumber.setText(GlobalApplication.getGlobalApplicationContext().getUserId());


        try {
            Uri uri = GlobalApplication.getGlobalApplicationContext().getProfileImage();

            Log.d(TAG, "Uri : " + uri);
            if(uri != null){
                iv_profile.setImageURI(GlobalApplication.getGlobalApplicationContext().getProfileImage());
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StatusBar statusBar = new StatusBar(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(parent[0]);

        mViewPager.setCurrentItem(0);

        // Pager setting
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(parent[position]);

                // http://www.hardcopyworld.com/ngine/android/index.php/archives/164
                //이전 페이지에 해당하는 페이지 표시 이미지 변경
                mPageMark.getChildAt(mPrevPosition).setBackgroundResource(R.drawable.page_not);

                //현재 페이지에 해당하는 페이지 표시 이미지 변경
                mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);
                mPrevPosition = position;                //이전 포지션 값을 현재로 변경

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initPageMark();

        if (!AlarmBroadCastReciever.isLaunched) {
            AlarmUtil.setSenderId(16);
            AlarmUtil.setReceiverId(17);
            AlarmUtil.setUserNickname(userNickname);
            AlarmUtil.setMessage("Test message dump");
            AlarmUtil.setTopic("Sajouiot02");

            AlarmUtil.getInstance().startMatchingAlarm(this);
        }else{

        }
    }

    private void initPageMark(){
        mPageMark = (LinearLayout)findViewById(R.id.page_mark);

        for(int i=0; i < parent.length; i++)
        {
            ImageView iv = new ImageView(getApplicationContext());
            iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            if(i==0){
                iv.setBackgroundResource(R.drawable.page_select);
            }else{
                iv.setBackgroundResource(R.drawable.page_not);
            }

            mPageMark.addView(iv);
        }
        mPrevPosition = 0;
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            count++;
            long time = intent.getLongExtra(KEY_DEFAULT,0);
            if (time > 0) {
                Date date = new Date(time);
                Log.d(TAG, "count : " + count);
            }
        }
    };


    @Override
    protected void onPause() {
        unregisterReceiver(mTimeReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(mTimeReceiver,new IntentFilter(INTENTFILTER_BROADCAST_TIMER));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_select_opponent, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_messageSetting:{
                Intent intent = new Intent(getApplicationContext(), ReserveActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_gallery:{

                break;
            }
            case R.id.nav_setting:{
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_callCenter:{

                break;
            }
            case R.id.nav_contract:{

                break;
            }
            default:{
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String userRole) {
            Log.d(TAG, "newInstance");

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(USER_ROLE, userRole);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView");

            int idx = getArguments().getInt(ARG_SECTION_NUMBER);
            String userRole = getArguments().getString(USER_ROLE);

            Log.d(TAG, "idx : " + idx);

            View rootView = null;

            if(userRole.equals("parent")) {
                rootView = inflater.inflate(R.layout.fragment_select_child, container, false);

                Button btn_call = (Button) rootView.findViewById(R.id.btn_call);
                Button btn_chat = (Button) rootView.findViewById(R.id.btn_chat);

                buttonListener(btn_call, btn_chat, new Button(getActivity()), idx);
            }else{
                rootView = inflater.inflate(R.layout.fragment_select_parent, container, false);

                Button btn_call = (Button) rootView.findViewById(R.id.btn_call);
                Button btn_chat = (Button) rootView.findViewById(R.id.btn_chat);
                Button btn_chat_setting = (Button) rootView.findViewById(R.id.btn_chat_setting);

                buttonListener(btn_call, btn_chat, btn_chat_setting, idx);

                TextView tv_timeTitle = (TextView) rootView.findViewById(R.id.tv_timeTitle);
                tv_timeTitle.setText("발송 예정");

                TextView tv_timeReserved = (TextView) rootView.findViewById(R.id.tv_timeReserved);
                tv_timeReserved.setText(timeReserved[idx]);



                TextView tv_messageReserved = (TextView) rootView.findViewById(R.id.tv_messageReserved);
                tv_messageReserved.setText(message[idx]);
            }


            RoundedImageView circularImageView = (RoundedImageView) rootView.findViewById(R.id.riv_opponentProfile);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId[idx]);

            int profileWidth = 800;
            int profileHeight = 800;

            bitmap = resizeBitmap(bitmap, profileWidth, profileHeight);
            //bitmap = cropBitmap(bitmap, 300, 300);

            circularImageView.setImageBitmap(bitmap);


            circularImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            circularImageView.setCornerRadius((float)20);
            circularImageView.setBorderWidth((float)2);
            circularImageView.setBorderColor(Color.DKGRAY);
            circularImageView.mutateBackground(true);


            return rootView;
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
                    Intent intent = new Intent(getActivity(), ChattingActivity.class);
                    //intent.putExtra("topic", parentArrayList.get(idx));
                    intent.putExtra(USER_PHONE_NUMBER, userPhoneNumber);
                    intent.putExtra(USER_NICKNAME, userNickname);
                    intent.putExtra("topic", topic[idx]);
                    intent.putExtra("parentName", parent[idx]);
                    intent.putExtra("opponentProfile", profile[idx]);
                    startActivity(intent);
                }
            });

            // Message Setting button
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ReserveActivity.class);
                    startActivity(intent);
                }
            });

        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem");

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, userRole);
        }

        @Override
        public int getCount() {
            return parent.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, "getPageTitle");
            return parent[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem");

            return super.instantiateItem(container, position);
        }
    }

    /**     http://bbulog.tistory.com/25
     *
     * 이미지를 주어진 너비와 높이에 맞게 리사이즈 하는 코드.
     * 원본 이미지를 크롭 하는게 아니라 리사이즈 하는 것이어서,
     * 주어진 너비:높이 의 비율이 원본 bitmap 의 비율과 다르다면 변환 후의 너비:높이의 비율도 주어진 비율과는 다를 수 있다.
     *
     * 가로가 넓거나 세로가 긴 이미지를 정사각형이나 원형의 view 에 맞추려 할 때,
     * 이 메쏘드를 호출한 후 반환된 bitmap 을 crop 하면 찌그러지지 않는 이미지를 얻을 수 있다.
     *
     * @param Bitmap bitmap 원본 비트맵
     * @param int width 뷰의 가로 길이
     * @param int height 뷰의 세로 길이
     *
     * @return Bitmap 리사이즈 된 bitmap

    public Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
    if (bitmap.getWidth() != width || bitmap.getHeight() != height){
    float ratio = 1.0f;

    if (width > height) {
    ratio = (float)width / (float)bitmap.getWidth();
    } else {
    ratio = (float)height / (float)bitmap.getHeight();
    }

    bitmap = Bitmap.createScaledBitmap(bitmap,
    (int)(((float)bitmap.getWidth()) * ratio), // Width
    (int)(((float)bitmap.getHeight()) * ratio), // Height
    true);
    }

    return bitmap;
    }
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap.getWidth() != width || bitmap.getHeight() != height){
            float widthRatio = 1.0f;
            float heightRatio = 1.0f;

            widthRatio = (float)width / (float)bitmap.getWidth();

            heightRatio = (float)height / (float)bitmap.getHeight();


            bitmap = Bitmap.createScaledBitmap(bitmap,
                    (int)(((float)bitmap.getWidth()) * widthRatio), // Width
                    (int)(((float)bitmap.getHeight()) * heightRatio), // Height
                    true);
        }

        return bitmap;
    }

    /**
     *
     * 이미지를 주어진 사이즈에 맞추어 crop 하는 코드
     * 원본의 가운데를 중심으로 crop 한다.
     *
     *
     * @param Bitmap bitmap 원본 비트맵
     * @param int width 뷰의 가로 길이
     * @param int height 뷰의 세로 길이
     *
     * @return Bitmap crop 된 bitmap
     */
    public Bitmap cropBitmap(Bitmap bitmap, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        // 이미지를 crop 할 좌상단 좌표
        int x = 0;
        int y = 0;

        if (originWidth > width) { // 이미지의 가로가 view 의 가로보다 크면..
            x = (originWidth - width)/2;
        }

        if (originHeight > height) { // 이미지의 세로가 view 의 세로보다 크면..
            y = (originHeight - height)/2;
        }

        Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        return cropedBitmap;
    }
}
