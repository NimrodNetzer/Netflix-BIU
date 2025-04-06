package com.example.netflix_android.View;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.netflix_android.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";

    private PlayerView playerView;
    private ExoPlayer player;
    private ImageView btnRewind, btnForward, btnPlayPause, btnBack;
    private LinearLayout controlsContainer;
    private SeekBar seekBar;
    private boolean isPlaying = true;
    private final Handler uiHandler = new Handler();
    private final Runnable hideUIRunnable = this::hideControls;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Fullscreen Setup
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        // ✅ Bind UI Elements
        playerView = findViewById(R.id.player_view);
        btnRewind = findViewById(R.id.btn_rewind);
        btnForward = findViewById(R.id.btn_forward);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnBack = findViewById(R.id.btn_back);
        controlsContainer = findViewById(R.id.controls_container);
        seekBar = findViewById(R.id.seek_bar);

        // ✅ Get Video URL from Intent
        String videoUrl = getIntent().getStringExtra("video_url");
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "⚠️ No video URL provided!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "❌ No video URL received. Exiting...");
            finish();
            return;
        }

        Log.d(TAG, "🎬 Playing video: " + videoUrl);

        // ✅ Initialize ExoPlayer
        initializePlayer(videoUrl);

        // ✅ Back Button Action (Disappears when idle)
        btnBack.setOnClickListener(v -> finish());

        // ✅ Button actions (Now SeekBar updates when moving forward/backward)
        btnRewind.setOnClickListener(v -> {
            if (player != null) {
                long newPosition = Math.max(player.getCurrentPosition() - 10000, 0);
                player.seekTo(newPosition);
                updateSeekBar(); // ✅ Update SeekBar
            }
        });

        btnForward.setOnClickListener(v -> {
            if (player != null) {
                long newPosition = Math.min(player.getCurrentPosition() + 10000, player.getDuration());
                player.seekTo(newPosition);
                updateSeekBar(); // ✅ Update SeekBar
            }
        });

        btnPlayPause.setOnClickListener(v -> {
            if (player != null) {
                if (isPlaying) {
                    player.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                } else {
                    player.play();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                }
                isPlaying = !isPlaying;
            }
        });

        // ✅ Handle SeekBar interactions
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null) {
                    long seekPosition = (player.getDuration() * progress) / 100;
                    player.seekTo(seekPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHandler.removeCallbacks(hideUIRunnable);
                showControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startUIAutoHide();
            }
        });

        // ✅ Toggle UI visibility on touch
        playerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                toggleControls();
                return true;
            }
            return false;
        });

        // ✅ Auto-hide UI after 3 seconds
        startUIAutoHide();
    }

    // ✅ Initialize ExoPlayer
    private void initializePlayer(String videoUrl) {
        try {
            Log.d(TAG, "initializePlayer: " + videoUrl);
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            playerView.setUseController(false); // ✅ Remove ExoPlayer's default UI

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);

            // ✅ Keep updating SeekBar with video progress
            uiHandler.postDelayed(updateSeekBarRunnable, 500);

        } catch (Exception e) {
            Toast.makeText(this, "⚠️ Failed to load video.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ Video playback error: " + e.getMessage());
        }
    }

    // ✅ Update SeekBar based on video progress
    private void updateSeekBar() {
        if (player != null && player.getDuration() > 0) {
            int progress = (int) ((player.getCurrentPosition() * 100) / player.getDuration());
            seekBar.setProgress(progress);
        }
    }

    // ✅ Runnable to update SeekBar every 500ms
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            uiHandler.postDelayed(this, 500);
        }
    };

    // ✅ Toggle visibility of UI controls (Including Back Button)
    private void toggleControls() {
        if (controlsContainer.getVisibility() == View.VISIBLE) {
            hideControls();
        } else {
            showControls();
            startUIAutoHide();
        }
    }

    // ✅ Show controls (Including Back Button)
    private void showControls() {
        controlsContainer.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }

    // ✅ Hide controls after a delay
    private void startUIAutoHide() {
        uiHandler.removeCallbacks(hideUIRunnable);
        uiHandler.postDelayed(hideUIRunnable, 3000); // 3 seconds auto-hide
    }

    // ✅ Hide UI controls (Including Back Button)
    private void hideControls() {
        controlsContainer.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
    }

    // ✅ Fullscreen Mode for Landscape
    private void enterFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    // ✅ Handle Orientation Change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreenMode();
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    // ✅ Properly release ExoPlayer when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
        }
    }
}
