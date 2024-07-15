package com.holndal.rendercomaliver

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.holndal.rendercomaliver.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private val reloadInterval = 300000L
    private val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tokyo")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebViews()
        startReloading()

        binding.button.setOnClickListener {
            update()
        }
    }

    private fun setupWebViews() {
        binding.wb1.loadUrl("https://google.com")
    }

    private fun update(){
        reloadWebViews()
        updateLastUpdatedTime()
    }

    private fun startReloading() {
        val reloadRunnable = object : Runnable {
            override fun run() {
                update()
                handler.postDelayed(this, reloadInterval)
            }
        }
        handler.post(reloadRunnable)
    }

    private fun reloadWebViews() {
        binding.wb1.reload()
        binding.wb2.reload()
        binding.wb3.reload()
    }

    private fun updateLastUpdatedTime() {
        val currentDate = sdf.format(Date())
        binding.lastUpdatedTimeText.text = currentDate
    }

    override fun onDestroy() {
        val intent = Intent(this@MainActivity, BackGroundRequestService::class.java)
        stopService(intent)
        handler.removeCallbacksAndMessages(null)
        _binding = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this@MainActivity, BackGroundRequestService::class.java)
        stopService(intent)
    }

    override fun onPause() {
        super.onPause()
        val intent = Intent(this@MainActivity, BackGroundRequestService::class.java)
        startService(intent)
    }


    override fun onStart() {
        super.onStart()
        val intent = Intent(this@MainActivity, BackGroundRequestService::class.java)
        stopService(intent)
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent(this@MainActivity, BackGroundRequestService::class.java)
        startService(intent)
    }
}
