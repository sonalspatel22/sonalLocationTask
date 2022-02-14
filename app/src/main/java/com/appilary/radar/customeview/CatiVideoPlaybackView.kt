package com.appilary.radar.customeview

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appilary.radar.R
import com.appilary.radar.activities.VideoPlaybackActivity
import com.appilary.radar.api.res.FormField
import com.appilary.radar.database.survey.ResAElement

/**
 * Created by vi.garg on 31/5/16.
 */
class CatiVideoPlaybackView(model: FormField) : CatiCustomView(model) {

    var videoPlayed = ""

    override fun setQuesLayout() {
        itemLayout.addView(addLayoutView())
    }

    fun setTheme(color: Int) {
        quesTv?.setTextColor(color)
    }

    override suspend fun fillData(): ResAElement? {
        if (videoPlayed.isEmpty()) {
            return null
        } else {
            return ResAElement(id).apply {
                singleAns = videoPlayed
            }
        }
    }

    private fun addLayoutView(): View {
        val view = inflater.inflate(R.layout.cati_video_playback, null)
        val title = view.findViewById<View>(R.id.title) as TextView
        val titleText = formField.desc
        if (TextUtils.isEmpty(titleText)) title.visibility = View.GONE else title.text = titleText
        view.findViewById<ImageView>(R.id.image_play).setOnClickListener {
            VideoPlaybackActivity.openActivity(mContext,
                formField.file ?: return@setOnClickListener,
                formField.enableforward == 1)
            videoPlayed = "true"
        }
        return view
    }

}