package macker.ltjh

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val doAnimate = AtomicBoolean(true)
    private lateinit var remoteEndpoint: RemoteEndpoint
    private lateinit var controlActivity: ControlActivity
    companion object {
        private const val bluetoothRequestCode = 1
        private const val wifiRequestCode = 2 // Wi-Fi P2P not implemented yet
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Call the function to start the animation
        startAnimatingIcons()

        // Initialize RemoteEndpoint with the return value of the button (Bluetooth), it goes to the
        // BluetoothDeviceListActivity and returns the selected device, which is then passed to the
        // RemoteEndpoint constructor
        // And, of course, the RemoteEndpoint is initialized with the BluetoothManager
        // next step, we use RemoteEndpoint.getDevice() to get the BluetoothDevice, and pass it to
        // the ControlActivityLayout constructor
        findViewById<Button>(R.id.bluetoothButton).setOnClickListener {
            if (::remoteEndpoint.isInitialized) {
                // pass to control activity directly
                controlActivity = ControlActivity(remoteEndpoint.getDevice())
            }
            val intent = Intent(this, BluetoothDeviceListActivity::class.java)
            startActivityForResult(intent, bluetoothRequestCode)
        }

        // Wi-Fi p2p is not implemented yet, so we'll just use Bluetooth for now
        // When the Wi-Fi button is clicked, we print a toast message saying that it's not
        // implemented yet, and we don't do anything else.
        findViewById<Button>(R.id.wifiButton).setOnClickListener {
            Toast.makeText(this, "Wi-Fi P2P not implemented yet", Toast.LENGTH_SHORT).show()
        }

        // Show the remoteEndpoint info on the screen, it's a textview on the left top corner
        // Create a new textview, and set the text to the remoteEndpoint.show() value
        val textView = TextView(this)
        textView.text = remoteEndpoint.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == bluetoothRequestCode && resultCode == Activity.RESULT_OK) {
            // Assuming the BluetoothDevice is passed back in the intent's extras
            val device: Any = data?.getParcelableExtra("selected_device")
                ?: run {
                    Log.d("MainActivity", "Failed to get device")
                    return
                }

            remoteEndpoint = RemoteEndpoint.create(device)
        } else if (requestCode == wifiRequestCode && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Wi-Fi P2P not implemented yet", Toast.LENGTH_SHORT).show()
        }
        else {
            Log.d("MainActivity", "Failed to get device")
        }

        // If remoteEndpoint is not initialized, we don't do anything
        if (!::remoteEndpoint.isInitialized) {
            Log.d("MainActivity", "RemoteEndpoint not initialized")
            return
        }

        controlActivity = ControlActivity(remoteEndpoint.getDevice())
    }

    override fun onPause() {
        super.onPause()
        doAnimate.set(false)
    }

    private fun startAnimatingIcons() {
        val frameLayout = findViewById<FrameLayout>(R.id.animatedBackground)
        val icon = ResourcesCompat.getDrawable(resources, R.drawable.icon, null) ?: run {
            Log.d("MainActivity", "Failed to load icon")
            return
        }

        Thread {
            while(doAnimate.get()) {
                runOnUiThread {
                    animateIcon(frameLayout, icon)
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun animateIcon(frameLayout: FrameLayout, icon: Drawable) {
        // Create the ImageView for the animation
        val imageView = ImageView(this).apply {
            setImageDrawable(icon)
            alpha = 0f // Start invisible
        }

        // Add the ImageView to the FrameLayout
        frameLayout.addView(imageView)

        // Randomize start delays and duration for a more natural effect
        val random = Random(System.currentTimeMillis())
        val startDelay = random.nextInt(1000).toLong()
        val duration = 5000 + random.nextInt(5000).toLong() // Between 5-10 seconds

        // Animate alpha and translation
        imageView.animate().alpha(1f).setStartDelay(startDelay).setDuration(duration).start()

        ObjectAnimator.ofFloat(imageView, "translationX", -frameLayout.width.toFloat()).apply {
            interpolator = LinearInterpolator()
        }.start()

        ObjectAnimator.ofFloat(imageView, "translationY", -frameLayout.height.toFloat()).apply {
            interpolator = LinearInterpolator()
        }.start()
    }
}
