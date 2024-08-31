package ua.teri.totp.utils.custom_info.profile_photo

import okhttp3.Request
import ua.teri.totp.utils.Http

class GithubProfilePhotoProvider(
    private val size: Int = 460
) : IProfilePhotoProvider() {
    private val http = Http()

    override fun getProfilePhotoUrl(username: String): String? {
        return try {
            http.execute(
                Request.Builder().url("https://github.com/$username.png?size=$size").head().build()
            ).headers["location"]
        } catch (_: Exception) {
            null
        }

    }
}

//