package com.appilary.radar.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.appilary.radar.R
import com.appilary.radar.api.ApiAction
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.frag.BaseFragment
import com.appilary.radar.listners.FileSelectedListner
import kotlinx.android.synthetic.main.frag_free_draw_image.*
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException


class FreeDrawImageFragment : BaseFragment() {
    private var fileUri: Uri? = null
    private var filePath: String? = null
    private var listener: FileSelectedListner? = null
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!
    }

    private fun setListener(listener: FileSelectedListner) {
        this.listener = listener
    }

    private fun setFilePath(fileUrl: String) {
        this.filePath = fileUrl
        this.fileUri = Uri.parse(fileUrl)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.frag_free_draw_image, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_view.setImageURI(fileUri)
        edit.setOnClickListener {
            img_view.inEditMode = true

            edit.visibility = View.GONE
        }


        ok.setOnClickListener {
            lifecycleScope.launch {
                if (edit.visibility == View.GONE) {
                    filePath?.let {
                        try {
                            FileOutputStream(it).use { out ->
                                img_view.drawableToBitmap()?.compress(
                                    Bitmap.CompressFormat.PNG,
                                    80,
                                    out
                                ) // bmp is your Bitmap instance
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }

                listener?.onFileSelected(fileUri, filePath)
                cancel()
            }
        }

    }

    fun cancel() {
        fragmentManager?.popBackStackImmediate()
    }

    override fun onErrorCall(event: ApiAction, data: BaseErrorResponse) {
    }

    override fun <T> onResponseCall(event: ApiAction, t: T) {
    }

    companion object {
        val TAG = FreeDrawImageFragment::class.java.simpleName
        fun newInstance(listener: FileSelectedListner, path: String): FreeDrawImageFragment {
            val dialog = FreeDrawImageFragment()
            dialog.setListener(listener)
            dialog.setFilePath(path)
            return dialog
        }
    }
}