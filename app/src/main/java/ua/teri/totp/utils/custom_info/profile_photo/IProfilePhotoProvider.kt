package ua.teri.totp.utils.custom_info.profile_photo

abstract class IProfilePhotoProvider {
    abstract fun getProfilePhotoUrl(username: String): String?
}