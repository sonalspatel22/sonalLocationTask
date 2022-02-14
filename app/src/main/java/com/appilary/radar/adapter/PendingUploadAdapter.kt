package com.appilary.radar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.appilary.radar.R
import com.appilary.radar.bean.UploadPendingBean
import com.appilary.radar.utils.AppUtils
import kotlinx.android.synthetic.main.item_pending_upload.view.*

class PendingUploadAdapter(val activity: FragmentActivity, val items: List<UploadPendingBean>) :
    RecyclerView.Adapter<PendingUploadAdapter.ViewHolder>() {

    val inflater = LayoutInflater.from(activity)

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.item_pending_upload,
                parent,
                false
            )
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.nameTv?.text = item.fileType
        holder.timeTv?.text = AppUtils.getDateTime(item.time)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv = view.name
        val timeTv = view.time
    }
}

