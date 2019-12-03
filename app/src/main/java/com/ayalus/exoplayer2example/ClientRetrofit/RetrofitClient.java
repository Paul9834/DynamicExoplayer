package com.ayalus.exoplayer2example.ClientRetrofit;

import com.ayalus.exoplayer2example.Controllers.CanalesIDInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://ott.terraformed.services/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public CanalesIDInterface getLogin() {
        return retrofit.create(CanalesIDInterface.class);
    }

}
