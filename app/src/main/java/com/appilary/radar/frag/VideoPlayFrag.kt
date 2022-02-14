package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import com.appilary.radar.R
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import kotlinx.android.synthetic.main.frag_video_play.*


/**
 * Created by vi.garg on 6/2/18.
 */
class VideoPlayFrag : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_video_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        video_view.setVideoURI(Uri.parse("android.resource://" + mainActivity.packageName + "/" + R.raw.doctor_video))

        val controller = MediaController(mainActivity)

        video_view.setMediaController(controller)

        video_view.setOnCompletionListener {
            mainActivity.onBackPressed()
        }

        video_view.requestFocus()

        video_view.start()

    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }


    companion object {
        val TAG = VideoPlayFrag::class.java.simpleName
        @JvmStatic
        fun newInstance(): VideoPlayFrag {
            val fragment = VideoPlayFrag()
            return fragment
        }
    }
}