package com.github.zzb.retrofitputdemo.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface RestService {

    /**
     * 没有使用，只是拿出来作对比
     */
    @Multipart
    @POST()
    Observable<ResponseBody> postFormVideoFile(@Url String url, @Part MultipartBody.Part file);

    @Multipart
    @PUT()
    Observable<ResponseBody> putFormVideoFile(@Url String url, @Part MultipartBody.Part file);


    @PUT()
    Observable<ResponseBody> putBodyVideoFile(@Url String url, @Body RequestBody file);
}
