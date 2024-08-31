package ua.teri.totp.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import dev.robinohs.totpkt.otp.HashAlgorithm
import dev.robinohs.totpkt.otp.OtpGenerator
import dev.robinohs.totpkt.otp.hotp.HotpGenerator
import dev.robinohs.totpkt.otp.totp.TotpGenerator

import ua.teri.totp.data.OtpInfo
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
class OTPGenerator(private val otpInfo: OtpInfo) {
    private var handler: ((String) -> Unit)? = null
    private val timer = Handler(Looper.getMainLooper())
    private val generator: OtpGenerator

    init {
        val algorithm = when (otpInfo.algorithm){
            "SHA1" -> HashAlgorithm.SHA1
            "SHA256" -> HashAlgorithm.SHA256
            "SHA512" -> HashAlgorithm.SHA512
            else -> HashAlgorithm.SHA1 // default
        }
        generator = when (otpInfo.protocol) {
            "totp" -> TotpGenerator(
                algorithm = algorithm,
                codeLength=otpInfo.digits,
                timePeriod=Duration.ofSeconds(otpInfo.period!!.toLong())
            )
            "hotp" -> HotpGenerator(
                algorithm=algorithm,
                codeLength=otpInfo.digits,
            )
            else -> throw Exception("Not support Protocol")
        }
    }

    private fun counter(): Long{
        return when (otpInfo.protocol) {
            "totp" -> System.currentTimeMillis()
            "hotp" -> otpInfo.counter!!.toLong()
            else -> throw Exception("Not support Protocol")
        }
    }

    fun remainingTime(): Long {
        if (otpInfo.protocol != "totp") return 0L
        return (generator as TotpGenerator).calculateRemainingTime(counter()).toMillis()
    }

    fun elapsedTime(): Long {
        return (otpInfo.period!!.toLong() * 1000L) - remainingTime()
    }

    fun generate(): String {
        return generator.generateCode(otpInfo.secret.encodeToByteArray(), counter())
    }

    fun setHandler(handler: (String) -> Unit): OTPGenerator {
        this.handler = handler
        return this
    }

    private val timeHandler = object : Runnable {
        override fun run() {
            val otp = generate()
            handler?.invoke(otp)
            timer.postDelayed(this, remainingTime())
        }
    }

    fun start() {
        if (otpInfo.protocol == "totp") {
            timer.postDelayed(timeHandler, remainingTime())
        } else {
            throw UnsupportedOperationException("Auto-generation is only supported for TOTP.")
        }
    }

    fun stop() {
        timer.removeCallbacks(timeHandler)
    }


}
