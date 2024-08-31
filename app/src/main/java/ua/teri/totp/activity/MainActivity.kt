package ua.teri.totp.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import ua.teri.totp.R
import ua.teri.totp.data.OtpInfo
import ua.teri.totp.databinding.ActivityMainBinding
import ua.teri.totp.utils.OtpInfoAdapter
import ua.teri.totp.utils.OtpInfoStorage
import ua.teri.totp.utils.custom_info.CacheOtpCustomInfo
import ua.teri.totp.utils.custom_info.ClearbitOtpCustomInfo
import ua.teri.totp.utils.custom_info.OtpCustomInfo
import ua.teri.totp.utils.custom_info.ProfilePhotoOtpCustomInfo
import ua.teri.totp.utils.otpAuthFromURL
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var otpStorage: OtpInfoStorage
    private lateinit var otpListAdapter: OtpInfoAdapter

    private val scanQrCodeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val otpInfo = data?.getParcelableExtra<OtpInfo>("otp_info")
                    ?: return@registerForActivityResult
                addOtpInfo(otpInfo)
            }
        }

    private fun addOtpInfo(otpInfo: OtpInfo?){
        if (otpInfo == null) return
        otpStorage.saveOtpInfo(otpInfo)
        otpListAdapter.update()
        val intent = Intent(this, OtpInfoActivity::class.java)
        intent.putExtra("otpInfo", otpInfo)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        otpStorage = OtpInfoStorage(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.scanQrCode.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java)
            scanQrCodeLauncher.launch(intent)
        }

        // Find RecyclerView by ID
        val recyclerView = findViewById<RecyclerView>(R.id.list)

        // Set LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the OTP info list
        val getOtpCustomInfo = CacheOtpCustomInfo(this)
        getOtpCustomInfo.setParent(ProfilePhotoOtpCustomInfo())?.setParent(ClearbitOtpCustomInfo())

        otpListAdapter = OtpInfoAdapter(
            otpStorage,
            getOtpCustomInfo
        )
        recyclerView.adapter = otpListAdapter

        otpListAdapter.update()

        if (intent?.dataString != null)
            addOtpInfo(otpAuthFromURL(intent.dataString!!))
    }

    override fun onResume() {
        super.onResume()
        otpListAdapter.update()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}