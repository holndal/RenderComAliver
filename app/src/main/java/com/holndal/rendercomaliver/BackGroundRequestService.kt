package com.holndal.rendercomaliver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BackGroundRequestService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val reloadInterval = 300000L

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startAccess()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun startForegroundService() {
        val channelId = "BackgroundServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Background Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Background Service")
            .setContentText("Service is running in the background")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun startAccess() {
        val reloadRunnable = object : Runnable {
            override fun run() {
                val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

                if(capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true){
                    makeNetworkRequest("https://google.com")
                }
                handler.postDelayed(this, reloadInterval)
            }
        }
        handler.post(reloadRunnable)
    }

    private fun makeNetworkRequest(urlString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(urlString)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.w("HTTP Request", "Failed to execute request", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.w("HTTP Request", "Success to execute request")
                }
            })
        }
    }
}