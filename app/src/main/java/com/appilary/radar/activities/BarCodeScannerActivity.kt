package com.appilary.radar.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appilary.radar.R
import com.appilary.radar.live_data.BarCodeLiveData
import com.appilary.radar.utils.AppUtils
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarCodeScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    private var flashState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    private fun initUI() {
        mScannerView = ZXingScannerView(this)
        lyt_barcode.addView(mScannerView)
        btnLight.setOnClickListener {
            if (flashState) {
                btnLight.setBackgroundResource(R.drawable.ic_flash_on)
                AppUtils.showToast(R.string.flashlight_turned_off)
                mScannerView?.flash = false
                flashState = false
            } else {
                btnLight.setBackgroundResource(R.drawable.ic_flash_off)
                AppUtils.showToast(R.string.flashlight_turned_on)
                mScannerView?.flash = true
                flashState = true
            }
        }
    }


    override fun handleResult(result: Result?) {
        BarCodeLiveData.postValue(result?.text?.toString())
        finish()
    }
}