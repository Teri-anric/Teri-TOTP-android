package ua.teri.totp.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import ua.teri.totp.R
import ua.teri.totp.data.OtpInfo
import ua.teri.totp.utils.OTPGenerator
import ua.teri.totp.utils.OtpInfoStorage
import ua.teri.totp.utils.custom_info.CacheOtpCustomInfo
import ua.teri.totp.utils.custom_info.ClearbitOtpCustomInfo
import ua.teri.totp.utils.custom_info.OtpCustomInfo
import ua.teri.totp.utils.custom_info.ProfilePhotoOtpCustomInfo
import ua.teri.totp.utils.generateQRCode

class OtpInfoActivity : AppCompatActivity() {
    private lateinit var otpInfo: OtpInfo
    private lateinit var otpStorage: OtpInfoStorage
    private lateinit var getOtpCustomInfo: OtpCustomInfo

    private lateinit var profilePhoto: ImageView
    private lateinit var platformPhoto: ImageView
    private lateinit var username: TextView
    private lateinit var platform: TextView

    private lateinit var qrCodeImageView: ImageView

    private lateinit var oneTimeCode: TextView
    private lateinit var codeTimeIndicator: LinearProgressIndicator

    private lateinit var otpGenerator: OTPGenerator
    private var showQrCodeStatus = false


    private val handler = Handler(Looper.getMainLooper())

    private val updateCodeTimeIndicator = object : Runnable {
        override fun run() {
            codeTimeIndicator.progress += 1
            handler.postDelayed(this, 100)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        qrCodeImageView = findViewById(R.id.qr_code_image_view)
        qrCodeImageView.setOnClickListener {
            showQrCode()
        }
        val deleteButton: Button = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            otpStorage.removeOtpInfo(otpInfo)
            finish()
        }

        otpInfo = intent.getParcelableExtra<OtpInfo>("otpInfo")
            ?: throw Exception("OtpInfoActivity not otpInfo param in intent extra")
        getOtpCustomInfo = CacheOtpCustomInfo(this).apply {
            setParent(ProfilePhotoOtpCustomInfo())?.setParent(ClearbitOtpCustomInfo())
        }
        otpStorage = OtpInfoStorage(this)

        otpGenerator = OTPGenerator(otpInfo)

        oneTimeCode = findViewById(R.id.one_time_code)
        codeTimeIndicator = findViewById(R.id.code_time_indicator)

        setOneTimeCode(otpGenerator.generate())
        codeTimeIndicator.max = (otpInfo.period ?: 0) * 10
        codeTimeIndicator.progress = (otpGenerator.elapsedTime() / 100L).toInt()

        if (otpInfo.protocol == "totp") {
            otpGenerator.setHandler {
                setOneTimeCode(it)
                codeTimeIndicator.progress = 0
            }
            handler.postDelayed(updateCodeTimeIndicator, otpGenerator.remainingTime() % 100L)
            otpGenerator.start()
        }
        createHeader()
    }

    private fun setOneTimeCode(code: String) {
        val size = if (code.length > 2) 1 else 3
        oneTimeCode.text = code.chunked(size).joinToString(" ")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        otpGenerator.stop()
        handler.removeCallbacks(updateCodeTimeIndicator)
    }

    private fun showQrCode() {
        showQrCodeStatus = !showQrCodeStatus
        if (showQrCodeStatus) {
            qrCodeImageView.setImageBitmap(
                generateQRCode(otpInfo.toUrlString(), 500, 500)
            )
        } else {
            qrCodeImageView.setImageResource(R.drawable.click_to_shared_qr_code)
        }
    }

    private fun createHeader() {
        profilePhoto = findViewById(R.id.profilePhoto)
        platformPhoto = findViewById(R.id.platformPhoto)
        username = findViewById(R.id.username)
        platform = findViewById(R.id.platform)

        username.text = otpInfo.username
        platform.text = otpInfo.issuer

        getOtpCustomInfo.getProfilePhotoUrl(otpInfo)?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(profilePhoto)
        }
        getOtpCustomInfo.getPlatformPhotoUrl(otpInfo)?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(platformPhoto)
        }
    }
}