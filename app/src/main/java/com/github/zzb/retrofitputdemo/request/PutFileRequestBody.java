package com.github.zzb.retrofitputdemo.request;

import android.content.Context;

import com.github.zzb.retrofitputdemo.api.RestClient;
import com.github.zzb.retrofitputdemo.callback.ProgressCallback;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/3/16.
 */
public class PutFileRequestBody {

    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("multipart/form-data");

    private Context mContext;
    private RequestBody requestBody;
    private File file;
    private MediaType mediaType;

    public PutFileRequestBody(Context mContext, File file){
        this.mContext = mContext;
        this.file = file;
    }

    private RequestBody buildRequestBody(){
        if(file != null && file.exists()){
            requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, file);
        }
        return requestBody;
    }

    private RequestBody wrapRequestBody(RequestBody requestBody, final ProgressCallback progressCallback) {
        if (progressCallback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener()
        {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                RestClient.getInstance(mContext).getDelivery().execute(new Runnable() {
                    @Override
                    public void run() {
                        progressCallback.onProgressChangeListener(bytesWritten, contentLength);
                    }
                });

            }
        });
        return countingRequestBody;
    }

    public RequestBody getRequestBody(ProgressCallback progressCallback) {
        requestBody = buildRequestBody();
        requestBody = wrapRequestBody(requestBody, progressCallback);
        return requestBody;
    }

}
