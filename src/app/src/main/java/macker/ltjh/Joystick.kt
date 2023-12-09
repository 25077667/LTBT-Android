package macker.ltjh

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("ClickableViewAccessibility")
class Joystick @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private var centerX: Float = 0f,
    private var centerY: Float = 0f
) : View(context, attrs, defStyleAttr) {

    private var baseRadius = 450f // Default base radius for the joystick
    private var hatRadius = 50f // Default hat (handle) radius for the joystick
    private var hatX = centerX
    private var hatY = centerY

    // True if the joystick is on the "left side" of the screen, false otherwise
    private var isLeftSide = centerX < (getScreenWidth() / 2f)

    private fun getScreenWidth(): Float {
        return context.resources.displayMetrics.widthPixels.toFloat()
    }

    private val paintBase = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val paintHat = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private var moveListener: OnMoveListener? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val diameter = (baseRadius * 2).toInt()
        setMeasuredDimension(diameter, diameter)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, baseRadius, paintBase)
        canvas.drawCircle(hatX, hatY, hatRadius, paintHat)
        super.onDraw(canvas)
    }

     fun updatePosition(x: Float, y: Float) {
        val distance = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        val angle = atan2(y - centerY, x - centerX)

        if (distance > baseRadius) {
            hatX = centerX + (baseRadius * kotlin.math.cos(angle))
            hatY = centerY + (baseRadius * kotlin.math.sin(angle))
        } else {
            hatX = x
            hatY = y
        }
        invalidate()

        // Notify the listener about joystick movement
        val normalizedAngle = if (angle < 0) angle + (2 * kotlin.math.PI).toFloat() else angle
        val normalizedDistance = min(distance, baseRadius) / baseRadius // Normalized distance in [0, 1]
        moveListener?.invoke(normalizedAngle, normalizedDistance, isLeftSide)
    }

     fun resetPosition() {
        hatX = centerX
        hatY = centerY
        invalidate()

        // Notify the listener about joystick reset
        moveListener?.invoke(0f, 0f, false)
    }

    fun setOnMoveListener(listener: OnMoveListener) {
        moveListener = listener
    }

//    OnMoveListener interface
    interface OnMoveListener {
        fun onMove(angle: Float, strength: Float, isLeftSide : Boolean)

        fun invoke(angle: Float, strength: Float, isLeftSide : Boolean) {
            onMove(angle, strength, isLeftSide)
        }
    }
}
