package com.example.zmx.facerecognitionattendancemanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener {

    //用于拍照
    private SubsamplingScaleImageView photo;
    private Uri imageUri;
    private File outputImage;

    public static final int TAKE_PHOTO = 1;

    //用于判断是register还是transmit
    private int request_flag;
    final int TRANSMIT = 0;
    final int REGISTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        //获取request_flag，-1表示错误
        Intent intent_flag = getIntent();
        request_flag = intent_flag.getIntExtra("request_flag", -1);

        Log.d("myactivity", String.valueOf(request_flag));
        Toast.makeText(TakePhotoActivity.this, String.valueOf(request_flag), Toast.LENGTH_SHORT).show();

        //ImageView photo用于显示拍好的图片
        photo = findViewById(R.id.photo);

        //提交按钮
        FloatingActionButton fab_upload_photo = findViewById(R.id.fab_photo_upload);
        fab_upload_photo.setOnClickListener(this);

        //outputImage用于存储拍照后的图片
        outputImage = new File(getExternalCacheDir(), "face_check_take_photo_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //不同安卓版本不同
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(TakePhotoActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent_photo = new Intent("android.media.action.IMAGE_CAPTURE");
        intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent_photo, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                switch (resultCode) {
                    case RESULT_OK:             //拍照成功后加载图片
                        photo.setImage(ImageSource.uri(imageUri));
                        break;
                    case RESULT_CANCELED:       //取消拍照后结束此Activity
                        TakePhotoActivity.this.finish();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_photo_upload:
                switch (request_flag) {
                    case REGISTER:
                        //若有图片，则提交
                        if (photo.hasImage()) {
                            Log.d("activity", "are you ok?");
                            //新建一个Dialog输入学生姓名（user_id）
                            final EditText editText = new EditText(TakePhotoActivity.this);
                            AlertDialog.Builder inputDialog =
                                    new AlertDialog.Builder(TakePhotoActivity.this);
                            inputDialog.setTitle("在此输入学生姓名").setView(editText);
                            inputDialog.setPositiveButton("提交",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //上传成功后消失
                                            UploadRequest(editText.getText().toString(), outputImage);
                                        }
                                    }).show();
                            break;
                        }
                    case TRANSMIT:
                        if(photo.hasImage()){
                            UploadRequest(null, outputImage);
                            finish();
                        }

                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }


    //注册上传函数，成功返回1，否则返回0
    private int UploadRequest(String user_id, File user_pictrue) {

        //请求url
        String url = "http://i229uk.natappfree.cc/";

        //自定义MEDIA_TYPE_PNG格式
        MediaType MEDIA_TYPE_PNG = MediaType.parse(user_pictrue.getName());
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //不同请求方式
        if (request_flag == REGISTER) {
            url = url + "register";
            builder.addFormDataPart("user_id", user_id);
            builder.addFormDataPart("register",
                    user_pictrue.getName(), RequestBody.create(MEDIA_TYPE_PNG, user_pictrue));
        } else if (request_flag == TRANSMIT) {
            url = url + "transmit";
            builder.addFormDataPart("transmit",
                    user_pictrue.getName(), RequestBody.create(MEDIA_TYPE_PNG, user_pictrue));

        }

        RequestBody requestBody = builder.build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("internet_error", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String response_data = response.body().toString();
                Log.d("internet_ok", response_data);
            }
        });
        return 1;
    }
}
