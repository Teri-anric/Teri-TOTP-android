package ua.teri.totp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import ua.teri.totp.R
import ua.teri.totp.activity.OtpInfoActivity
import ua.teri.totp.data.OtpInfo
import ua.teri.totp.utils.custom_info.OtpCustomInfo

class OtpListItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val profilePhoto: ImageView
    private val platformPhoto: ImageView
    private val username: TextView
    private val platform: TextView
    private lateinit var otpInfo: OtpInfo
    private var interactive = true

    init {
        LayoutInflater.from(context).inflate(R.layout.otp_list_item, this, true)
        profilePhoto = findViewById(R.id.profilePhoto)
        platformPhoto = findViewById(R.id.platformPhoto)
        username = findViewById(R.id.username)
        platform = findViewById(R.id.platform)
        setOnClickListener {
            if (interactive) {
                val intent = Intent(context, OtpInfoActivity::class.java)
                intent.putExtra("otpInfo", otpInfo)
                context.startActivity(intent)
            }
        }
    }

    fun setInteractive(status: Boolean) {
        interactive = status
    }

    @SuppressLint("UseCompatLoadingForDrawables", "CheckResult")
    fun bind(otpInfo: OtpInfo, getCustomInfo: OtpCustomInfo) {
        this.otpInfo = otpInfo
        username.text = otpInfo.username
        platform.text = otpInfo.issuer


        Glide.with(context)
            .load(getCustomInfo.getProfilePhotoUrl(otpInfo) ?: context.getDrawable(R.drawable.circle_user))
            .circleCrop()
            .into(profilePhoto)

        Glide.with(context)
            .load(getCustomInfo.getPlatformPhotoUrl(otpInfo) ?: context.getDrawable(R.drawable.ic_company))
            .circleCrop()
            .into(platformPhoto)

    }
}
