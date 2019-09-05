package com.ayalus.exoplayer2example;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/*
Created by: Ayal Fieldust
Date: 8/2017

Description:
This Example app was created to show a simple example of ExoPlayer Version 2.8.4.
There is an option to play mp4 files or live stream content.
Exoplayer provides options to play many different formats, so the code can easily be tweaked to play the requested format.
Scroll down to "ADJUST HERE:" I & II to change between sources.
Keep in mind that m3u8 files might be stale and you would need new sources.
 */
public class MainActivity extends AppCompatActivity implements VideoRendererEventListener {

    private static final String TAG = "MainActivity";
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private TextView resolutionTextView;
    ConcatenatingMediaSource concatenatedSource;
    private RequestQueue mQueue;
    DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource;
    ArrayList<String> links;
    int lastWindowIndex = 0;
    Button boton;

    private View debugRootView;

    DataSource.Factory dataSourceFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolutionTextView = new TextView(this);
        resolutionTextView = (TextView) findViewById(R.id.resolution_textView);
        test();

        boton = findViewById(R.id.exo_next);



//// I. ADJUST HERE:
////CHOOSE CONTENT: LiveStream / SdCard
//
////LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:
//
////        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
////        Uri mp4VideoUri =Uri.parse("http://54.255.155.24:1935//Live/_definst_/amlst:sweetbcha1novD235L240P/playlist.m3u8"); //Radnom 540p indian channel
//        Uri mp4VideoUri =Uri.parse("http://cbsnewshd-lh.akamaihd.net/i/CBSNHD_7@199302/index_700_av-p.m3u8"); //CNBC
        Uri mp4VideoUri = Uri.parse("http://live.field59.com/wwsb/ngrp:wwsb1_all/playlist.m3u8"); //ABC NEWS
////        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------
//
//
////VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
////        String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
////        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

        int h = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int w = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;
        Log.v(TAG, "height : " + h + " weight: " + w);
        ////Set media controller
        simpleExoPlayerView.setUseController(true);//set to true or false to see controllers
        simpleExoPlayerView.requestFocus();
        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
//        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(liveStreamUri);

        //// II. ADJUST HERE:

        ////        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
        ////Produces Extractor instances for parsing the media data.
        //        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //This is the MediaSource representing the media to be played:
        //FOR SD CARD SOURCE:
        //        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String uriCopy = prefs.getString("url", "no id");


        Log.e("URL :", uriCopy);


        //FOR LIVESTREAM LINK:


        MediaSource videoSource = new HlsMediaSource(Uri.parse("http://192.168.201.3:5080/LiveApp/streams/914405587199755935241300.m3u8"), dataSourceFactory, 1, null, null);
        MediaSource videoSource2 = new HlsMediaSource(Uri.parse("http://192.168.201.3:5080/LiveApp/streams/968916636641591144924592.m3u8"), dataSourceFactory, 1, null, null);
        MediaSource videoSource3 = new HlsMediaSource(Uri.parse("http://192.168.201.3:5080/LiveApp/streams/809910696262825011485157.m3u8"), dataSourceFactory, 1, null, null);


        mQueue = Volley.newRequestQueue(this);


        String url = "http://headendredir.terraformed.services/canales/1";




        links = new ArrayList<String>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("playlist");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);
                                String id = employee.getString("id");
                                String name = employee.getString("name");
                                String link = employee.getString("link");



                                links.add(link);

                            }



                            ArrayList<MediaSource> sources = new ArrayList<>();
                            for (int i=0;i< links.size();i++) {
                                MediaSource mediaSource = new HlsMediaSource(Uri.parse(links.get(i)), dataSourceFactory, 1, null, null);
                                sources.add(mediaSource);
                            }
                            dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
                            dynamicConcatenatingMediaSource.addMediaSources(sources);
                            // concatenatedSource = new ConcatenatingMediaSource(videoSource, videoSource2, videoSource3);
                            // Prepare the player with the source.


                            for (int i = 0; i < links.size(); i++) {
                                Log.e("HOLAAA", links.get(i));
                            }

                            player.prepare(dynamicConcatenatingMediaSource);


                            boton = findViewById(R.id.exo_next);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);

        player.addListener(new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }


            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState+"|||isDrawingCacheEnabled():"+simpleExoPlayerView.isDrawingCacheEnabled());
            }



            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "Listener-onPlayerError...");
                player.prepare(dynamicConcatenatingMediaSource);
                player.setPlayWhenReady(true);
            }


            @Override
            public void onPositionDiscontinuity(int reason) {

                int latestWindowIndex = player.getCurrentWindowIndex();
                if (latestWindowIndex != lastWindowIndex) {
                    // item selected in playlist has changed, handle here
                    lastWindowIndex = latestWindowIndex;
                    // ...
                }



            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(links);
        editor.putString("task list", json);
        editor.apply();
    }

    public void test() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://headendredir.terraformed.services/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanalesIDInterface canalesIDInterface = retrofit.create(CanalesIDInterface.class);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");


        Call<List<PostCanalesID>> call = canalesIDInterface.getPosts(id);
        call.enqueue(new Callback<List<PostCanalesID>>() {

            @Override
            public void onResponse(Call<List<PostCanalesID>> call, retrofit2.Response<List<PostCanalesID>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<PostCanalesID> posts = response.body();

                String url = "";

                for (PostCanalesID post : posts) {
                    String content = "";
                    content += "link : " + post.getalt_uri() + "\n";
                    url = post.getalt_uri();
                }


                //// HEREE ///
                Log.e("url del ID ", url);


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", url); //InputString: from the EditText
                editor.apply();


            }

            @Override
            public void onFailure(Call<List<PostCanalesID>> call, Throwable t) {
            }
        });
    }


    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }



    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
        resolutionTextView.setText("RES:(WxH):" + width + "X" + height + "\n           " + height + "p");//shows video info
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }




//-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }


}

