package ua.teri.totp.activity

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.teri.totp.R
import ua.teri.totp.data.OtpInfo
import ua.teri.totp.utils.OtpInfoStorage
import java.io.FileOutputStream
import java.io.InputStreamReader

class SettingActivity : AppCompatActivity() {

    private lateinit var otpStorage: OtpInfoStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otpStorage = OtpInfoStorage(this)

        findViewById<Button>(R.id.export_to_json).setOnClickListener {
            selectExportFileLauncher.launch("teri_otp_dump.json")
        }

        findViewById<Button>(R.id.import_from_json).setOnClickListener {
            selectImportFileLauncher.launch(arrayOf("application/json"))
        }

    }

    private val selectImportFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                importOtpInfo(uri)
            }
        }

    // Function to import OTP information from a selected file
    private fun importOtpInfo(uri: Uri) {
        val contentResolver: ContentResolver = this.contentResolver

        contentResolver.openInputStream(uri)?.use { inputStream ->
            val gson = Gson()
            val reader = InputStreamReader(inputStream)
            val otpListType = object : TypeToken<List<OtpInfo>>() {}.type
            val importedOtpInfoList: List<OtpInfo> = gson.fromJson(reader, otpListType)
            var overrideCount = 0

            importedOtpInfoList.forEach {
                otpStorage.getOtpInfo(it.platform, it.username)?.let {
                    overrideCount += 1
                }
                otpStorage.saveOtpInfo(it)
            }
            Snackbar.make(
                findViewById(R.id.main),
                "Load ${importedOtpInfoList.count()} opt, override $overrideCount",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


    private val selectExportFileLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri?.let {
                exportOtpInfo(uri)
            }
        }

    private fun exportOtpInfo(uri: Uri) {
        val contentResolver: ContentResolver = this.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { pfd: ParcelFileDescriptor ->
            FileOutputStream(pfd.fileDescriptor).use { outputStream ->
                val gson = Gson()
                val listOtpInfo = otpStorage.getAllOtpInfo()
                val jsonData = gson.toJson(listOtpInfo)
                outputStream.write(jsonData.toByteArray())
                Snackbar.make(
                    findViewById(R.id.main),
                    "Save ${listOtpInfo.count()} otp",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

}