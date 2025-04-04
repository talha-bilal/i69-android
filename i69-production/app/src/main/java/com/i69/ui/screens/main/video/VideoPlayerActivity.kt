package com.i69.ui.screens.main.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.i69.R
import com.i69.databinding.ActivityCameraBinding
import com.i69.databinding.ActivityVideoPlayerBinding
import com.i69.utils.CountDownTimerExt
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class VideoPlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    private var videoURL: String = ""
    private var timer1: CountDownTimerExt? = null
    private var isFirst: Boolean = false
    private var mCurrentPlaySpeed: Float = 1f
    private val handler = Handler(Looper.getMainLooper())

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)
        // Keep the screen on while the video is playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        supportActionBar?.hide()
        videoURL = intent.getStringExtra(VIDEO_URL) ?: ""
        initializePlayer()
        initListener()
    }

    private fun initListener() {
        binding.videoDisable.setOnClickListener {
            if (exoPlayer != null) {
                if (exoPlayer!!.isPlaying) {
                    binding.imgPlay.visibility = View.VISIBLE
                    binding.imgPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            this, R.drawable.ic_baseline_play_circle_outline_24
                        )
                    )
                    exoPlayer!!.pause()
                    timer1!!.pause()
                } else {
                    binding.imgPlay.visibility = View.GONE
                    binding.imgPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            this, R.drawable.ic_baseline_pause_circle_outline
                        )
                    )
                    exoPlayer!!.play()
                    if (timer1 != null) {
                        timer1?.start()
                    }
                }
            }
        }

        binding.playAudioSpeed.setOnClickListener {
            handlePlayAudioSpeed()
            exoPlayer?.setPlaybackSpeed(mCurrentPlaySpeed)
        }

        binding.closeBtn.setOnClickListener {
            if (exoPlayer != null) {
                exoPlayer?.pause()
            }
            this@VideoPlayerActivity.finish()
        }

        binding.progressBar1.setOnTouchListener { v, event -> true }
        // Add a listener to the range slider
//        binding.progressBar1.addOnChangeListener { slider, _, _ ->
//            val startMs = slider.values[0]
//            val endMs = slider.values[1]
//            exoPlayer?.seekTo(startMs.toLong())
//        }
    }


    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.videoview.player = exoPlayer
            val mediaItem =
                MediaItem.fromUri(Uri.parse(videoURL))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.addListener(playerListener)
        }
    }

    override fun onStart() {
        super.onStart()
        exoPlayer?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.playWhenReady = false
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_ENDED -> {
                    finishScreen()
                }

                Player.STATE_READY -> {
                    if (exoPlayer?.duration!! > 0) {
                        handleReadyState()
                    }
                }
            }
        }

//        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
//            val durationMs = exoPlayer?.duration
//            binding.progressBar1.valueFrom = 0f
//            binding.progressBar1.valueTo = durationMs!!.toFloat()
//        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e("TAG", "onPlayerError: ${error.message}")
        }
    }


    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun handleReadyState() {
        binding.progressBar.visibility = View.GONE
        if (!isFirst) {
            isFirst = true
            binding.playAudioSpeed.visibility = View.VISIBLE
            binding.durationTV.visibility = View.VISIBLE
            exoPlayer?.setPlaybackSpeed(mCurrentPlaySpeed)
            val realDurationMillis: Long = exoPlayer!!.duration
            binding.progressBar1.valueFrom = 0f
            binding.progressBar1.valueTo = 100f
            binding.progressBar1.stepSize = 1.0f
            binding.durationTV.visibility = View.VISIBLE

            timer1 = object : CountDownTimerExt(realDurationMillis, 100) {
                override fun onTimerTick(millisUntilFinished: Long) {

                    if (exoPlayer?.isPlaying == true) {
                        val diffTime: Long = realDurationMillis - millisUntilFinished
                        val elepsedTime: Long = 0 + diffTime

                        val currentPosition = exoPlayer?.currentPosition!!
                        val duration = exoPlayer?.duration!!
                        if (duration > 0) {
                            val progress =
                                (currentPosition / duration.toFloat() * 100).roundToInt().toFloat()
                            binding.progressBar1.values = listOf(progress)
                        }
                        binding.durationTV.text = formatTime(currentPosition)
                    }
                }

                override fun onTimerFinish() {
                    finishScreen()
                }
            }
            timer1!!.start()
        } else {
            if (exoPlayer!!.isPlaying) {
                timer1!!.pause()
            } else {
                timer1!!.start()
            }
        }
    }

    private fun finishScreen() {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
        }
        this@VideoPlayerActivity.finish()
    }


    private fun startSliderUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                if (exoPlayer?.isPlaying == true) {
                    // Adjust the current position by the playback speed
                    val playbackSpeed = exoPlayer?.playbackParameters?.speed
                    val adjustedPosition = exoPlayer?.currentPosition?.times(playbackSpeed!!)

                    // Update the RangeSlider's position
                    binding.progressBar1.setValues(
                        adjustedPosition,
                        binding.progressBar1.values[1]
                    )

                    // Repeat this runnable to keep the slider in sync
                    handler.postDelayed(this, 100L)
                }
            }
        })
    }

    @SuppressLint("NewApi")
    private fun handlePlayAudioSpeed() {
        when (binding.playAudioSpeed.text) {
            "1x" -> {
                mCurrentPlaySpeed = 1.5f
                binding.playAudioSpeed.text = "1.5x"
            }

            "1.5x" -> {
                mCurrentPlaySpeed = 2f
                binding.playAudioSpeed.text = "2x"
            }

            "2x" -> {
                mCurrentPlaySpeed = 1f
                binding.playAudioSpeed.text = "1x"
            }
        }
    }


    companion object {
        const val VIDEO_URL = "VIDEO_URL"
        fun startVideoPlayer(activity: Context, videoURL: String) {
            val intent = Intent(activity, VideoPlayerActivity::class.java)
            intent.putExtra(VIDEO_URL, videoURL)
            activity.startActivity(intent)
        }
    }


    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        exoPlayer?.release()
        super.onDestroy()
        exoPlayer = null
    }
}