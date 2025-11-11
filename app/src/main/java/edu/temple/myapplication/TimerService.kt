package edu.temple.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log

@Suppress("ControlFlowWithEmptyBody")
class TimerService : Service() {

    private var isRunning = false
    private var timerHandler: Handler? = null
    private lateinit var t: TimerThread
    private var paused = false
    private var currentValue = 1

    inner class TimerBinder : Binder() {

        val isRunning: Boolean
            get() = this@TimerService.isRunning

        val paused: Boolean
            get() = this@TimerService.paused

        fun start(maxValue: Int) {
            if (!paused) {
                if (!isRunning) {
                    if (::t.isInitialized) t.interrupt()
                    this@TimerService.start(maxValue)
                }
            } else {
                pause()
            }
        }

        fun setHandler(handler: Handler) {
            timerHandler = handler
        }

        fun stop() {
            if (::t.isInitialized && isRunning) {
                t.interrupt()
            }
        }

        fun pause() {
            this@TimerService.pause()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TimerService status", "Created")
    }

    override fun onBind(intent: Intent): IBinder {
        return TimerBinder()
    }

    fun start(maxValue: Int) {
        t = TimerThread(maxValue)
        t.start()
    }

    fun pause() {
        if (::t.isInitialized) {
            paused = !paused
            isRunning = !paused
        }
    }

    inner class TimerThread(private val maxValue: Int) : Thread() {
        override fun run() {
            isRunning = true
            try {
                for (i in currentValue..maxValue) {
                    if (!isRunning) break
                    if (!paused) {
                        currentValue = i
                        Log.d("Count", i.toString())
                        timerHandler?.sendEmptyMessage(i)
                        sleep(1000)
                    } else {
                        while (paused) sleep(200)
                    }
                }
                isRunning = false
            } catch (e: InterruptedException) {
                Log.d("Timer interrupted", e.toString())
                isRunning = false
                paused = false
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (::t.isInitialized) {
            t.interrupt()
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TimerService status", "Destroyed")
    }
}
