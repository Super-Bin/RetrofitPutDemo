package com.github.zzb.retrofitputdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.zzb.retrofitputdemo.api.RestClient;
import com.github.zzb.retrofitputdemo.callback.ProgressCallback;
import com.github.zzb.retrofitputdemo.request.PutFileRequestBody;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private TextView tv_text;
    private Button btn_putBody, btn_putForm;

    private RequestBody putFileRequestBody;
    public static final String url = "http://172.16.0.245:8200/video/test.mp4";
    File file = new File(Environment.getExternalStorageDirectory(), "nmp.mp4");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_text = (TextView) findViewById(R.id.tv_text);

        btn_putBody = (Button) findViewById(R.id.btn_putBody);
        btn_putForm = (Button) findViewById(R.id.btn_putForm);

        btn_putBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putBodyUpload();
            }
        });

        btn_putForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putFormUpload();
            }
        });

        initRequestBody();
    }

    private void initRequestBody() {

        ProgressCallback progressCallback = new ProgressCallback(){
            /**
             * UI线程
             * @param progress 当前进度
             * @param total 总长度
             */
            @Override
            public void onProgressChangeListener(long progress, long total) {
                tv_text.setText("我的上传进度:" + progress + ":" + total);
            }
        };
        putFileRequestBody = new PutFileRequestBody(this, file).getRequestBody(progressCallback);
    }

    public void putFormUpload(){
        String url = "http://172.16.0.245:8200/video/putForm.mp4";
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), putFileRequestBody);
        RestClient.getInstance(this).getRectService().putFormVideoFile(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "postUpload onCompleted: " );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "postUpload onError: " + e);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "postUpload onNext: ");
                    }
                });
    }

    public void putBodyUpload(){
        String url = "http://172.16.0.245:8200/video/putBody.mp4";
        RestClient.getInstance(this).getRectService().putBodyVideoFile(url, putFileRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "postUpload onCompleted: " );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "postUpload onError: " + e);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "postUpload onNext: ");
                    }
                });
    }
}
