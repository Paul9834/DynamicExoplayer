package com.ayalus.exoplayer2example;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CanalesIDInterface {


    @GET("canalalt")
    Call<List<PostCanalesID>> getPosts();

    @GET("canalalt/{id}")
    Call<List<PostCanalesID>> getPosts(@Path("id") String id);


}


