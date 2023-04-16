package macker.ltjh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import macker.ltjh.bluetooth.BluetoothActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start BluetoothActivity
        startActivity(Intent(this, BluetoothActivity::class.java))
    }
}