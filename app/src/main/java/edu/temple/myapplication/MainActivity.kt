package edu.temple.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Handler

class MainActivity : AppCompatActivity() {

    // ✅ Define handler before using it
    private val handler = Handler { msg ->
        println("Count: ${msg.what}")
        true
    }

    private var timerService: TimerService.TimerBinder? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerService = service as TimerService.TimerBinder
            timerService?.setHandler(handler)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            timerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this, TimerService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)

            if (isBound) {
                timerService?.start(100)
            }
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isBound) {
                timerService?.pause() // toggle pause/resume
            }
        }
    }
}
