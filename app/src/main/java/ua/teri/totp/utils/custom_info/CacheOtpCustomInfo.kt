package ua.teri.totp.utils.custom_info

import android.content.Context
import android.content.SharedPreferences
import ua.teri.totp.data.OtpInfo

class CacheOtpCustomInfo(context: Context) : OtpCustomInfo() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("OtpCustomInfoCache", Context.MODE_PRIVATE)

    override fun getProfilePhotoUrl(otpInfo: OtpInfo): String? {
        val key = "${otpInfo.platform}:${otpInfo.username}"
        val cachedUrl = sharedPreferences.getString(key, null)
        if (cachedUrl != null) {
            return cachedUrl
        }

        val profilePhotoUrl = super.getProfilePhotoUrl(otpInfo)

        if (profilePhotoUrl != null) {
            sharedPreferences.edit().putString(key, profilePhotoUrl).apply()
        }

        return profilePhotoUrl
    }

    override fun getPlatformPhotoUrl(otpInfo: OtpInfo): String? {
        val cachedUrl = sharedPreferences.getString(otpInfo.platform, null)
        if (cachedUrl != null) {
            return cachedUrl
        }

        val platformPhotoUrl = super.getPlatformPhotoUrl(otpInfo)

        if (platformPhotoUrl != null) {
            sharedPreferences.edit().putString(otpInfo.platform, platformPhotoUrl).apply()
        }

        return platformPhotoUrl
    }
}
