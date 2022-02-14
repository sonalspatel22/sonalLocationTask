package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.squareup.picasso.Picasso
import com.zolad.zoominimageview.ZoomInImageViewAttacher
import kotlinx.android.synthetic.main.frag_image_view.*


/**
 * Created by vi.garg on 6/2/18.
 */
class ImageViewFrag : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            var url = arguments?.getString("url")
            if (url.isNullOrEmpty())
                url = "dummy"

            Picasso.with(mainActivity)
                    .load(url)
                    .placeholder(R.drawable.gallery)
                    .error(R.drawable.gallery)
                    .into(image_view)

            val mIvAttacter = ZoomInImageViewAttacher()
            mIvAttacter.attachImageView(image_view)
        }

        main_view.setOnClickListener {
            mainActivity.onBackPressed()
        }
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }


    companion object {
        val TAG = ImageViewFrag::class.java.simpleName
        @JvmStatic
        fun newInstance(url: String): ImageViewFrag {
            val fragment = ImageViewFrag()
            val bundle = Bundle()
            bundle.putString("url", url)
            fragment.arguments = bundle
            return fragment
        }
    }
}