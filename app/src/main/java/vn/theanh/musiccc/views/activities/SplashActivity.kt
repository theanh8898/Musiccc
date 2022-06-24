package vn.theanh.musiccc.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import vn.theanh.musiccc.R

class SplashActivity : AppCompatActivity() {
    private var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val CODE_PERMISSION = 1998

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            )
        }
        checkPermission()
    }

    private fun checkPermission() {
        when (isPermissionsGranted(this)) {
            true -> openMainActivity()
            false -> requestPermissions()
        }
    }

    private fun isPermissionsGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissions
                .filter { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
                .forEach { Log.d("SplashActivity", "$it not granted"); return false }
            return true
        }
        return true
    }

    private fun openMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode == CODE_PERMISSION && isPermissionsGranted(this)) {
            true -> openMainActivity()
            else -> finish()
        }
    }
}