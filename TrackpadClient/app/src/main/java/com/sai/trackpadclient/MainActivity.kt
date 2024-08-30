package com.sai.trackpadclient

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private lateinit var outputStream: OutputStream
    private var lastX = 0f
    private var lastY = 0f
    private var socket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ipAddressInput = findViewById<EditText>(R.id.ipAddressInput)
        val connectButton = findViewById<Button>(R.id.connectButton)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)


        connectButton.setOnClickListener {
            val ipAddress = ipAddressInput.text.toString()
            connectToServer(ipAddress, 12345)
        }


        findViewById<Button>(R.id.scroll_up_button).setOnClickListener {
            sendCommand("scroll,0,-10")
        }

        findViewById<Button>(R.id.scroll_down_button).setOnClickListener {
            sendCommand("scroll,0,10")
        }


        findViewById<Button>(R.id.left_click_button).setOnClickListener {
            sendCommand("left_click")
        }

        findViewById<Button>(R.id.right_click_button).setOnClickListener {
            sendCommand("right_click")
        }

        findViewById<View>(R.id.touchpad_area).setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.x - lastX).toInt()
                    val deltaY = (event.y - lastY).toInt()

                    sendCommand("move,$deltaX,$deltaY")

                    lastX = event.x
                    lastY = event.y
                }
            }
            true
        }
    }

    private fun connectToServer(ipAddress: String, port: Int) {
        thread {
            try {
                socket?.close()
                socket = Socket(ipAddress, port)
                outputStream = socket!!.getOutputStream()

                runOnUiThread {
                    findViewById<TextView>(R.id.statusTextView).text = "Status: Connected"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    findViewById<TextView>(R.id.statusTextView).text = "Status: Connection Failed"
                }
            }
        }
    }

    private fun sendCommand(command: String) {
        thread {
            try {
                if (this::outputStream.isInitialized) {
                    outputStream.write("$command\n".toByteArray())
                    outputStream.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
