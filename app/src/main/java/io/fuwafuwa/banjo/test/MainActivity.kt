package io.fuwafuwa.banjo.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.fuwafuwa.banjo.extension.Frame
import io.fuwafuwa.banjo.extension.T
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Frame.init(windowManager)
        T.loggable = true
        button1.setOnClickListener {
            startActivity(Intent(this, DefaultRangeBarActivity::class.java))
        }
        button2.setOnClickListener {
            startActivity(Intent(this, BanjoActivity::class.java))
        }
    }
}
