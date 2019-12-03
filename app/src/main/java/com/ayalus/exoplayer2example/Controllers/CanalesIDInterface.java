package com.ayalus.exoplayer2example.Controllers;

import com.ayalus.exoplayer2example.Entities.PostCanalesID;
import com.ayalus.exoplayer2example.Entities.UserLogin;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CanalesIDInterface {


    @GET("canalalt")
    Call<List<PostCanalesID>> getPosts();

    @GET("canalalt/{id}")
    Call<List<PostCanalesID>> getPosts(@Path("id") String id);


    @FormUrlEncoded
    @POST("api/mobile/login")
    Call<List<UserLogin>> login(
            @Field("email") String email,
            @Field("password") String password);
}


