package macker.ltjh

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class ControlActivityLayout : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                return isClick(event)
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isClick(event: MotionEvent): Boolean {
        val touchSlop = 5 // Define your threshold for a click
        return event.x < touchSlop && event.y < touchSlop
    }
}