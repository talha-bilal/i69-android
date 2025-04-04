package com.i69.ui.screens.main.search.userProfile

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.RangeSlider
import com.i69.R
import com.i69.utils.CountDownTimerExt
import com.i69.utils.createLoadingDialog
import com.i69.utils.loadImage


class PicViewerFragment : DialogFragment() {

    private lateinit var views: View
    private lateinit var loadingDialog: Dialog
    private var TAG: String = PicViewerFragment::class.java.simpleName

    private var timer1: CountDownTimerExt? = null

    var exoPlayer: ExoPlayer? = null
    private var videoDisable: View? = null
    private var videoview: PlayerView? = null
    private var videoLayout: RelativeLayout? = null

    lateinit var progressBar1: RangeSlider
    private lateinit var imgplay: AppCompatImageView
    lateinit var progress_bar: CircularProgressIndicator

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        views = inflater.inflate(R.layout.fragment_pic_viewer, container, false)

        loadingDialog = requireActivity().createLoadingDialog()
        videoview = views.findViewById(R.id.videoview)
        videoLayout = views.findViewById(R.id.videoLayout)
        videoDisable = views.findViewById(R.id.videoDisable)
        imgplay = views.findViewById(R.id.img_play)
        progressBar1 = views.findViewById(R.id.progressBar1)
        val imgUserStory = views.findViewById<ImageView>(R.id.imgUserStory)
        val img_close = views.findViewById<ImageView>(com.i69.R.id.img_close)
        progress_bar = views.findViewById(R.id.progress_bar)

        val type = arguments?.getString("mediatype")
        val url = arguments?.getString("url", "")

        progressBar1.setOnTouchListener { v, event -> true }

        if (type.equals("image")) {
            imgUserStory.visibility = View.VISIBLE
            imgplay.visibility = View.GONE
            videoLayout!!.visibility = View.GONE
            if (!url.equals("")) {
                imgUserStory.loadImage(url!!)

            } else {
                imgUserStory.loadImage(R.drawable.ic_default_user)
            }
        } else if (type.equals("video")) {
            videoview!!.visibility = View.VISIBLE
            videoLayout!!.visibility = View.VISIBLE
            progressBar1.visibility = View.VISIBLE
            imgplay.visibility = View.GONE
            imgplay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_baseline_pause_circle_outline
                )
            )

            val uri = Uri.parse(url)

            initExoPlayer(uri)

        }


        img_close.setOnClickListener {
            releasePlayer()
            dismiss()
        }

        videoDisable!!.setOnClickListener {
            if (exoPlayer != null) {
                if (exoPlayer!!.isPlaying) {
                    imgplay.visibility = View.VISIBLE
                    imgplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_play_circle_outline_24
                        )
                    )
                    exoPlayer!!.pause()
                    timer1!!.pause()
                } else {
                    imgplay.visibility = View.GONE
                    imgplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_pause_circle_outline
                        )
                    )
                    exoPlayer!!.play()
                    timer1!!.start()
                }
            }
        }


        return views
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        dismiss()
    }


    protected fun showProgressView() {
        loadingDialog.show()
    }

    protected fun hideProgressView() {
        loadingDialog.dismiss()
    }

    var isFirst: Boolean = false

    fun initExoPlayer(videouri: Uri?) {

        // showProgressView()
        progress_bar.visibility = View.VISIBLE

        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        videoview!!.setPlayer(exoPlayer)
        val mediaItem = MediaItem.fromUri(videouri!!)
        exoPlayer!!.setMediaItem(mediaItem)
        exoPlayer!!.prepare()
        exoPlayer!!.play()

        val durationSet = false

        exoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {

                    //    hideProgressView()
                    progress_bar.visibility = View.GONE

                    if (!isFirst) {
                        isFirst = true
                        val realDurationMillis: Long = exoPlayer!!.getDuration()
                        progressBar1.valueFrom = 0.0f
                        progressBar1.valueTo = realDurationMillis.toFloat()

                        val millisInFuture = realDurationMillis
                        timer1 = object : CountDownTimerExt(millisInFuture, 100) {
                            override fun onTimerTick(millisUntilFinished: Long) {

                                val diffTime: Long = millisInFuture - millisUntilFinished
                                val elepsedTime: Long = 0 + diffTime

                                Log.e(TAG, "onTimerTick $millisUntilFinished")
                                onTickProgressUpdate(elepsedTime)
                            }

                            override fun onTimerFinish() {
                                dismiss()
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
            }
        })
    }

    private fun onTickProgressUpdate(long: Long) {
        progressBar1.setValues(long.toFloat())
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer!!.stop()
            exoPlayer!!.release()
        }
        if (timer1 != null) {
            timer1!!.pause()
        }
    }

}







