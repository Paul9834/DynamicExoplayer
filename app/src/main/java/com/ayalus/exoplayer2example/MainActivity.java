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
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
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

import java.util.ArrayList;
import java.util.List;

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


/**
 * Reproducción de ExoPlayer a través de microservicio Rest con links dinamicos..
 *
 * @author  Kevin Paul Montealegre Melo
 * @version 1.3
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

        // 1. Se llama al metodo Rest

        linkDinamicos();

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Crea el reproductor

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

        int h = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int w = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;
        Log.v(TAG, "height : " + h + " weight: " + w);

        // 3. ¿Desea tener controladores?

        simpleExoPlayerView.setUseController(true);//set to true or false to see controllers
        simpleExoPlayerView.requestFocus();
        simpleExoPlayerView.setPlayer(player);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeter);


        // 4. Obtenemos el id de los canales //

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String uriCopy = prefs.getString("url", "no id");
        //   Log.e("URL :", uriCopy);


        // 5. Metodo REST para obtener los canales dinamicos //

        mQueue = Volley.newRequestQueue(this);
        String url = "http://headendredir.terraformed.services/canales/2";
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
                                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(links.get(i)));
                                sources.add(videoSource);
                            }
                            dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
                            dynamicConcatenatingMediaSource.addMediaSources(sources);
                            for (int i = 0; i < links.size(); i++) {
                                Log.e("HOLAAA", links.get(i));
                            }
                            player.prepare(dynamicConcatenatingMediaSource);
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
                     lastWindowIndex = latestWindowIndex;
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
    public void linkDinamicos() {
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
                //   Log.e("url del ID ", url);
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