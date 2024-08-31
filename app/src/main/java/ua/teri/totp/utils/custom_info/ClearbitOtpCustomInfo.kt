package ua.teri.totp.utils.custom_info

import ua.teri.totp.data.OtpInfo
import ua.teri.totp.utils.Http

class ClearbitOtpCustomInfo : OtpCustomInfo() {
    private val http = Http()
    private val platformLogoSize = 80

    private fun getAvailableUrl(url: String): Boolean {
        return try {
            http.head(url).isSuccessful
        } catch (_: Exception){
            false
        }
    }

    private fun getPlatformLogoUrl(platform: String, domain: String = "com"): String {
        return "https://logo.clearbit.com/$platform.$domain?size=$platformLogoSize" // greyscale=
    }

    override fun getPlatformPhotoUrl(otpInfo: OtpInfo): String? {
        val url = getPlatformLogoUrl(otpInfo.issuer)
        if (getAvailableUrl(url))
            return url
        return super.getPlatformPhotoUrl(otpInfo)
    }
}
