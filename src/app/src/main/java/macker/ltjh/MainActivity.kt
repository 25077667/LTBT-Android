package macker.ltjh

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val doAnimate = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Call the function to start the animation
        startAnimatingIcons()
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
