package ua.teri.totp.utils.custom_info

import ua.teri.totp.data.OtpInfo

open class OtpCustomInfo {
    private var parent: OtpCustomInfo? = null;

    fun setParent(newParent: OtpCustomInfo? = null): OtpCustomInfo? {
        if (newParent == this) {
            throw IllegalArgumentException("Cannot set self as parent.")
        }
        if (newParent?.isAncestorOf(this) == true) {
            throw IllegalArgumentException("Cyclic dependency detected.")
        }
        this.parent = newParent
        return newParent
    }


    private fun isAncestorOf(child: OtpCustomInfo?): Boolean { // тест на циклічну рекурсію
        var current = child
        while (current != null) {
            if (current == this) {
                return true
            }
            current = current.parent
        }
        return false
    }

    open fun getProfilePhotoUrl(otpInfo: OtpInfo): String? {
        return parent?.getProfilePhotoUrl(otpInfo)
    }

    open fun getPlatformPhotoUrl(otpInfo: OtpInfo): String? {
        return parent?.getPlatformPhotoUrl(otpInfo)
    }
}
