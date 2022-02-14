package com.appilary.radar.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.appilary.radar.R
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class VideoPlaybackActivity : AppCompatActivity(), Player.EventListener {

    var isVideoFinish = true
    var simpleExoplayer: SimpleExoPlayer? = null
    var exoplayerView: PlayerView? = null
    var progressBar: ProgressBar? = null
    var url: String? = null
    private var playbackPosition: Long = 0

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_playback)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        exoplayerView = findViewById(R.id.exoplayerView)
        progressBar = findViewById(R.id.progressBar)

        url = intent.getStringExtra("url")
        isVideoFinish = intent.getBooleanExtra("isForwardEnable", false)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        preparePlayer()
        exoplayerView?.player = simpleExoplayer
        simpleExoplayer?.seekTo(playbackPosition)
        simpleExoplayer?.playWhenReady = true
        simpleExoplayer?.addListener(this)
        exoplayerView?.useController = isVideoFinish
    }


    private fun preparePlayer() {
        val uri = Uri.parse(url)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        simpleExoplayer?.prepare(mediaSource)
    }

    private fun releasePlayer() {
        simpleExoplayer?.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar?.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY)
            progressBar?.visibility = View.INVISIBLE
        else if (playbackState == Player.STATE_ENDED) {
            progressBar?.visibility = View.INVISIBLE
            isVideoFinish = true
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (isVideoFinish) {
            releasePlayer()
            super.onBackPressed()
        }
    }

    companion object {
        fun openActivity(mContext: Context, url: String, isForwardEnable: Boolean) {
            Intent(mContext, VideoPlaybackActivity::class.java).apply {
                putExtra("url", url)
                putExtra("isForwardEnable", isForwardEnable)
                mContext.startActivity(this)
            }
        }
    }
}