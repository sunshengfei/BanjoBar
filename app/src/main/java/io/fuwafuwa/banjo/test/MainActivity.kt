package io.fuwafuwa.banjo.test

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import io.fuwafuwa.banjo.extension.ActivityRequest
import io.fuwafuwa.banjo.extension.ContentUtil
import io.fuwafuwa.banjo.extension.Frame
import io.fuwafuwa.banjo.extension.T
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var currentType = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Frame.init(windowManager)
        T.loggable = true
        button1.setOnClickListener {
            currentType = 0
            beforeSelect()
        }
        button1.visibility = GONE
        button2.setOnClickListener {
            currentType = 1
            beforeSelect()
        }

    }

    private fun startActivityForSelect(uri: Uri?) {
        if (currentType == 0) {
            val intent: Intent = Intent(this, DefaultRangeBarActivity::class.java)
            intent.putExtra("url", ContentUtil.getPath(this, uri))
            startActivity(intent)
        } else {
            val intent: Intent = Intent(this, BanjoActivity::class.java)
            intent.putExtra("url", ContentUtil.getPath(this, uri))
            startActivity(intent)
        }
    }


    fun beforeSelect() {
        if (ActivityRequest.hasReadPermission(this)) {
            ActivityRequest.requestMediaVideo(this)
        } else {
            ActivityRequest.requestExternalStorageRead(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ActivityRequest.REQUEST_READ_LOCAL_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActivityRequest.requestMediaVideo(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (requestCode == ActivityRequest.REQUEST_LOCAL_VIDEO
            || requestCode == ActivityRequest.REQUEST_LOCAL_MEDIA
        ) {
            val uri = data.data
            //            String url = ContentUtil.getPath(mContext, uri);
            startActivityForSelect(uri);
        }
    }

}
