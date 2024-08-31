package ua.teri.totp.utils.custom_info;

import ua.teri.totp.data.OtpInfo
import ua.teri.totp.utils.custom_info.profile_photo.IProfilePhotoProvider
import ua.teri.totp.utils.custom_info.profile_photo.GithubProfilePhotoProvider
import ua.teri.totp.utils.custom_info.profile_photo.StaticProfilePhotoProvider

class ProfilePhotoOtpCustomInfo : OtpCustomInfo() {

    private val providers = mutableMapOf<String, IProfilePhotoProvider>(
        "GitHub" to GithubProfilePhotoProvider(240),
        "Freelancehunt" to StaticProfilePhotoProvider("https://content.freelancehunt.com/profile/photo/225/%s.png")
    )

    override fun getProfilePhotoUrl(otpInfo: OtpInfo): String? {
        val url = providers[otpInfo.issuer]?.getProfilePhotoUrl(otpInfo.username)
        if (url != null)
            return url
        return super.getProfilePhotoUrl(otpInfo)
    }
}
