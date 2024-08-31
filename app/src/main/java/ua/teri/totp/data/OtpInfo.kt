package ua.teri.totp.data;

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// otpauth://

@Parcelize
data class OtpInfo(
    val protocol: String,  // "totp" or "hotp"
    val platform: String,
    val username: String,
    val secret: String,
    val issuer: String,
    val algorithm: String,
    val digits: Int,
    var period: Int? = null,  // only TOTP
    var counter: Int? = null,  // only HOTP
) : Parcelable {
    init {
        if (protocol == "totp" && period == null)
            this.period = 30
        if (protocol == "hotp" && counter == null)
            this.counter = 1
    }

    fun toUri(): Uri {
        val builder = Uri.Builder()
            .scheme("otpauth")
            .authority(protocol)
            .appendPath("$platform:$username")
            .appendQueryParameter("secret", secret)
            .appendQueryParameter("digits", digits.toString())
            .appendQueryParameter("issuer", issuer)
            .appendQueryParameter("algorithm", algorithm)
        when (protocol){
            "totp" -> builder.appendQueryParameter("period", period.toString())
            "hotp" -> builder.appendQueryParameter("counter", counter.toString())
        }
        return builder.build()
    }
    fun toUrlString():String{
        return toUri().toString()
    }
}

