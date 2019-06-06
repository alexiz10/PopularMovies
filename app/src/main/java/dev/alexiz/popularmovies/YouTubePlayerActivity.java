package dev.alexiz.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubePlayerActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final String YT_API_KEY = "AIzaSyBifuMykWpJM2WJ9DBPmTi8_YIDjbb_tmw";

    private static final String TRAILER_INTENT_KEY = "trailer";

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private String trailerKey;

    YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        Intent intent = getIntent();

        if (intent.hasExtra(TRAILER_INTENT_KEY)) {
            this.trailerKey = intent.getStringExtra(TRAILER_INTENT_KEY);
        }

        this.youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);
        this.youTubePlayerFragment.initialize(YT_API_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing YouTubePlayer (%1$s)", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(trailerKey);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(YT_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_player_fragment);
    }

}