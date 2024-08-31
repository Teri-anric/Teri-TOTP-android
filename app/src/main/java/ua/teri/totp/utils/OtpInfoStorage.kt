package ua.teri.totp.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import ua.teri.totp.data.OtpInfo;

class OtpInfoStorage(context: Context) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "otp_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    private fun createKey(otpInfo: OtpInfo): String {
        return "${otpInfo.platform}_${otpInfo.username}"
    }

    fun saveOtpInfo(otpInfo: OtpInfo) {
        val json = gson.toJson(otpInfo)
        with(sharedPreferences.edit()) {
            putString(createKey(otpInfo), json)
            apply()
        }
    }

    fun getOtpInfo(platform: String, username: String): OtpInfo? {
        val key = "${platform}_$username"
        val json = sharedPreferences.getString(key, null)
        return json.let { gson.fromJson(it, OtpInfo::class.java) }
    }

    fun getAllOtpInfo(): List<OtpInfo> {
        val allEntries = sharedPreferences.all
        return allEntries.mapNotNull { (_, value) ->
            gson.fromJson(value as String, OtpInfo::class.java)
        }
    }

    fun removeOtpInfo(platform: String, username: String) {
        val key = "${platform}_$username"
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    fun removeOtpInfo(otpInfo: OtpInfo) {
        return removeOtpInfo(otpInfo.platform, otpInfo.username)
    }

    fun clearAllOtpInfo() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
