package com.appilary.radar.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appilary.radar.R
import com.appilary.radar.activities.CommonActivity
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.appilary.radar.utils.SETTING_SCREEN_OPEN
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bottomsheet_setting.*

class SettingBottomSheet : BottomSheetDialogFragment() {

    companion object {
        val TAG = SettingBottomSheet::class.java.simpleName

        fun getInstance(): SettingBottomSheet {
            return SettingBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottomsheet_setting, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_tv.setOnClickListener {
            activity?.let {
                CommonActivity.open(it, SETTING_SCREEN_OPEN)
            }
            dismiss()
        }

        remove_retain_tv.setOnClickListener {
            AppPreference.instance.setRetainValueData(null)
            AppUtils.showToast("Data Cleared")
            dismiss()
        }

    }

}