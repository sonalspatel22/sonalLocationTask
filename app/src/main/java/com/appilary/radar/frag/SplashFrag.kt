package com.appilary.radar.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppPreference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_splash.*

class SplashFrag : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppPreference.instance.getLoginData()?.let {
            if (!it.splashImg.isNullOrEmpty()) {
                Picasso.with(mainActivity)
                    .load(it.splashImg)
                    .into(splash_logo)
            }
        }


    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    companion object {
        val TAG = SplashFrag::class.java.simpleName
        val ROUTE_ARGS = "route_arg"

        @JvmStatic
        fun newInstance(): SplashFrag {
            val fragment = SplashFrag()
            return fragment
        }
    }
}