package net.vrgsoft.videcrop.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.VideoSize;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.ui.TimeBar;

public class VideoPlayer implements Player.Listener, TimeBar.OnScrubListener {

    private ExoPlayer player;
    private OnProgressUpdateListener mUpdateListener;
    private Handler progressHandler;
    private Runnable progressUpdater;

    public VideoPlayer(Context context) {
        // Create a DefaultTrackSelector with AdaptiveTrackSelection.Factory
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        trackSelector.setParameters(
                trackSelector.buildUponParameters()
                        .setMaxVideoSizeSd()
        );

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build();

        // Set repeat mode
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.addListener(this);

        // Initialize progress handler
        progressHandler = new Handler();
    }

    public void initMediaSource(Context context, String uri) {
        // Create MediaItem
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(uri));

        // Set the media item to be played
        player.setMediaItem(mediaItem);

        // Prepare the player
        player.prepare();
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void play(boolean play) {
        player.setPlayWhenReady(play);
        if (!play) {
            removeUpdater();
        }
    }

    public void release() {
        player.release();
        removeUpdater();
        player = null;
    }

    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, int reason) {
        updateProgress();
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        updateProgress();
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        if (mUpdateListener != null) {
            mUpdateListener.onFirstTimeUpdate(player.getDuration(), player.getCurrentPosition());
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        updateProgress();
    }

    //    @Override
//    public void onPlayerError(PlaybackException error) {
//    }

    @Override
    public void onScrubStart(TimeBar timeBar, long position) {
    }

    @Override
    public void onScrubMove(TimeBar timeBar, long position) {
        seekTo(position);
        updateProgress();
    }

    @Override
    public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        seekTo(position);
        updateProgress();
    }

    private void updateProgress() {
        if (mUpdateListener != null) {
            mUpdateListener.onProgressUpdate(
                    player.getCurrentPosition(),
                    player.getDuration() == C.TIME_UNSET ? 0L : player.getDuration(),
                    player.getBufferedPosition()
            );
        }
        initUpdateTimer();
    }

    private void initUpdateTimer() {
        long position = player.getCurrentPosition();
        int playbackState = player.getPlaybackState();
        long delayMs;

        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }

            removeUpdater();
            progressUpdater = () -> updateProgress();
            progressHandler.postDelayed(progressUpdater, delayMs);
        }
    }

    private void removeUpdater() {
        if (progressUpdater != null) {
            progressHandler.removeCallbacks(progressUpdater);
        }
    }

    public void seekTo(long position) {
        player.seekTo(position);
    }

    public void setUpdateListener(OnProgressUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public interface OnProgressUpdateListener {
        void onProgressUpdate(long currentPosition, long duration, long bufferedPosition);

        void onFirstTimeUpdate(long duration, long currentPosition);
    }
}
