package com.example.daily_report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubePlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youTubePlayerView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_play);


        youTubePlayerView = findViewById(R.id.youtube_play_view);

        intent =getIntent();
        if(intent.getStringExtra("youtubeVideoId")!=null){
            youTubePlayerView.initialize(YouTubeSearchActivity.API_KEY,this);
        }

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(intent.getStringExtra("youtubeVideoId"));
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        Toast.makeText(YouTubePlayActivity.this, "재생 실패", Toast.LENGTH_LONG).show();
    }
}