package macker.ltjh.controlPanel

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import macker.ltjh.R

class ControlPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_panel)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


    }
}