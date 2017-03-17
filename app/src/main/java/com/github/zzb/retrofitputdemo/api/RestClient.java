package com.github.zzb.retrofitputdemo.api;

import android.content.Context;
import android.util.Log;

import com.github.zzb.retrofitputdemo.utils.Platform;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/3/16.
 */
public class RestClient {

    private Retrofit mRetrofit;

    private static final String BASE_URL = "http://172.16.0.245/";
    private static RestClient mInstance;
    private RestService mService;
    private Context mContext;
    private Platform mPlatform;

    private RestClient(Context context) {
        mContext = context;
        mPlatform = Platform.get();
        initRestClint(BASE_URL);
    }

    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public static RestClient getInstance(Context context){
        if(mInstance == null){
            synchronized (RestClient.class){
                mInstance = new RestClient(context);
            }
        }
        return mInstance;
    }

    public void initRestClint(String baseUrl) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(new Cache(new File(mContext.getCacheDir()
                        .getAbsolutePath(), "cache"), 50 * 1024))
                .addInterceptor(new LogInterceptor());

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        mService = mRetrofit.create(RestService.class);
    }


    public RestService getRectService() {
        if (mService != null) {
            return mService;
        }
        return null;
    }

    private class LogInterceptor implements Interceptor {

        public final String TAG = LogInterceptor.class.getSimpleName();

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.i(TAG, "-----------------request:" + request.toString() + ", request Body: " + (request.body() == null ? "null" : request.body().toString()));
            long t1 = System.nanoTime();
            Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            Log.i(TAG, String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s%n",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.i(TAG, "Response body:" + content);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    }
}
