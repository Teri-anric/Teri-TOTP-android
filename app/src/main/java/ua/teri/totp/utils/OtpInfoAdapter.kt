package ua.teri.totp.utils

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.teri.totp.data.OtpInfo
import ua.teri.totp.ui.OtpListItem
import ua.teri.totp.utils.custom_info.OtpCustomInfo
import kotlin.math.max

class OtpInfoAdapter(
    private val otpInfoStorage: OtpInfoStorage,
    private val otpCustomInfo: OtpCustomInfo
) : RecyclerView.Adapter<OtpInfoAdapter.OtpViewHolder>() {

    private var otpInfoList: List<OtpInfo> = emptyList()

    // ViewHolder class for your custom view
    class OtpViewHolder(val itemView: OtpListItem) : RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtpViewHolder {
        // Inflate the custom view and return the ViewHolder
        val itemView = OtpListItem(parent.context)
        return OtpViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OtpViewHolder, position: Int) {
        // Bind data to the custom view
        val otpInfo = otpInfoList[position]
        (holder.itemView as OtpListItem).bind(otpInfo, otpCustomInfo)
    }

    override fun getItemCount(): Int {
        return otpInfoList.size
    }

    fun update(){
        val oldCount = otpInfoList.count()
        otpInfoList = otpInfoStorage.getAllOtpInfo()
        notifyItemRangeChanged(0, max(oldCount, otpInfoList.count()))
    }
}
