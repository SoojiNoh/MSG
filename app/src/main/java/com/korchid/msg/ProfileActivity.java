package com.korchid.msg;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

// Modify and register profile image
public class ProfileActivity  extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ProfileActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_IMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_profile;

    private Button btn_upload;

    private int viewId;
    private String absolutePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StatusBar statusBar = new StatusBar(this);

        CustomActionbar customActionbar = new CustomActionbar(this, R.layout.actionbar_content, "Profile");

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);

        //iv_userPhoto.setImageBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Echo/Images/"+file_name);
    }

    // Take pictures function
    // http://jeongchul.tistory.com/287
    public void takePictureAction(){
        Log.d(TAG, "takePictureAction");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Temp path
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri =  Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public  void takeAlbumAction(){
        Log.d(TAG, "takeAlbumAction");
        // Call Album
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult / request code : " + requestCode + ", result code : " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            //Toast.makeText(ProfileActivity.this, "RESULT ERROR : " + resultCode, Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM:
            {
                Log.d(TAG, "PICK_FROM_ALBUM case");
                mImageCaptureUri = data.getData();
                //Toast.makeText(ProfileActivity.this, mImageCaptureUri.getPath().toString(), Toast.LENGTH_LONG).show();
                //iv_profile.setImageURI(mImageCaptureUri);
                //Log.e("Album", mImageCaptureUri.getPath().toString());
            }
            case PICK_FROM_CAMERA:
            {
                Log.d(TAG, "PICK_FROM_CAMERA case");
                // Resize image
                // call crop application
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // Store cropping image 200*200
                intent.putExtra("outputX", 200); // x size
                intent.putExtra("outputY", 200); // y size
                intent.putExtra("aspectX", 1); // x proportion
                intent.putExtra("aspectY", 1); // y proportion
                intent.putExtra("scale", true);
                intent.putExtra("returnData", true);
                //iv_userPhoto.setImageURI(mImageCaptureUri);
                startActivityForResult(intent, CROP_IMAGE); // Move CROP_IMAGE case
                break;
            }
            case CROP_IMAGE:
            {
                Log.d(TAG, "CROP_IMAGE case");
                // Receive image cropped
                // Additional work and delete temp file
                if(resultCode != RESULT_OK){
                    Toast.makeText(ProfileActivity.this, "CROP ERR : " + mImageCaptureUri.getPath().toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                final Bundle extras = data.getExtras();

                // Save image cropped
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MSG/" + System.currentTimeMillis() + ".jpg";
                Log.e("PATH", filePath);

                if(extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    iv_profile.setImageBitmap(photo);

                    storeCropImage(photo, filePath);
                    absolutePath = filePath;
                    break;
                }

                File file = new File(mImageCaptureUri.getPath());

                GlobalApplication.getGlobalApplicationContext().setProfileImage(mImageCaptureUri);

                if(file.exists()){
                    file.delete();
                }
            }
        }

    }

    @Override
    public void onClick(View v){
        viewId = v.getId();

        switch (viewId){
            case R.id.btn_register:{
                // Use Environmental variable 'SharedPreference'
                SharedPreferences sharedPreferences = getSharedPreferences("login", 0);

                // if sharedPreferences.getString value is 0, assign 2th parameter
                String login = sharedPreferences.getString("USER_LOGIN", "LOGOUT");
                String facebookLogin = sharedPreferences.getString("FACEBOOK_LOGIN", "LOGOUT");
                String userId = sharedPreferences.getString("USER_ID", "");
                String userName = sharedPreferences.getString("USER_NAME", "");
                String userPassword = sharedPreferences.getString("USER_PASSWORD", "");
                String userPhone = sharedPreferences.getString("USER_PHONE", "");
                String userEmail = sharedPreferences.getString("USER_EMAIL", "");

                //DB query


                //Intent intent = new Intent(ChattingSubActivity.this, LoginActivity.class);
                //ChattingSubActivity.this.startActivity(intent);
                //ChattingSubActivity.this.finish();
                Toast.makeText(this, "Complete join", Toast.LENGTH_LONG).show();

                break;
            }
            case R.id.btn_upload:{
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePictureAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("Choose Image to upload")
                        .setPositiveButton("Take pictures", cameraListener)
                        .setNeutralButton("Choose album", albumListener)
                        .setNegativeButton("Cancel", cancelListener)
                        .show();
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
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        return true;
    }

    // Save Bitmap image
    private void storeCropImage(Bitmap bitmap, String filePath){
        Log.d(TAG, "storeCropImage");
        // Create MSG directory and save image
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MSG";
        File directoryMSG = new File(dirPath);

        if(!directoryMSG.exists()){
            directoryMSG.mkdir();
        }

        File copyFile = new File(filePath);
        BufferedOutputStream outputStream = null;

        try{
            copyFile.createNewFile();
            outputStream = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // sendBroadcast - renew image cropped
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            outputStream.flush();
            outputStream.close();

        }catch (Exception e){
            Log.e(TAG, "storeCropImage Error : " + e.getMessage());
            e.printStackTrace();
        }

    }



}
