package ua.teri.totp.utils.custom_info.profile_photo

import ua.teri.totp.utils.Http

class StaticProfilePhotoProvider(
    private val url: String
) : IProfilePhotoProvider() {
    private val http = Http()

    override fun getProfilePhotoUrl(username: String): String? {
        try {
            if (http.head(url.format(username)).isSuccessful)
                return url.format(username)
            return null
        } catch (_: Exception) {
            return null
        }
    }
}