package io.fuwafuwa.banjo.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.fuwafuwa.banjo.extension.Frame
import io.fuwafuwa.banjo.model.Size
import io.fuwafuwa.banjo.extension.T
import io.fuwafuwa.banjo.ui.ThumbNaiUnit
import io.fuwafuwa.banjo.extension.VideoThumbNailExtractor
import kotlinx.android.synthetic.main.activity_default.*
import java.io.File
import java.util.*

class DefaultRangeBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        Frame.init(windowManager)

        buttonTest.setOnClickListener {
            if (percent > 0) {
                percent = 0f
                scroll()
            } else {
                percent = 1f
            }
        }

        buttonTest1.setOnClickListener {
            startNormalClip()
        }

    }

    val list: MutableList<ThumbNaiUnit> = ArrayList()

    var dataSource2 = "/sdcard/Movies/1998-Bravo-AllStars-Let-The-Music-Heal-Your-Soul.mp4"

    //    var dataSource = "/sdcard/Movies/d.1.mp4"
//    var dataSource = "/sdcard/Movies/ddd.mp4"
    var dataSource = "/sdcard/Movies/A_10_1.mp4"

    //    var dataSource = "/sdcard/Movies/1111.mp4"
    var frameSize = Size(90, 120)
    val callback = object : VideoThumbNailExtractor.Callback {
        override fun onThumbReceived(index: Int, location: String) {
            runOnUiThread {
                var unit = list.get(index)
                unit.thumbUrl = location
                rangeBar.notifyDataChanged(index)
            }
        }

        override fun onSkeletonPrepared(thumbsSlot: List<Long?>?) {
            runOnUiThread {
                list.clear()
                for (i in 1..thumbsSlot!!.size) {
                    val unit = ThumbNaiUnit()
                    unit.timePoint = thumbsSlot.get(i - 1)!!
                    list.add(unit)
                }
                rangeBar.dataSet(list)
                rangeBar.postDelayed(Runnable {
                    rangeBar.addThumb(0f, -1f)
//            rangeBar.addThumb()
                }, 200)
            }

        }
    }

    private fun startNormalClip() {
        var videoName = File(dataSource).name

        var outputDir = getExternalFilesDir("")?.absolutePath + File.separator + videoName

        Thread() {
            VideoThumbNailExtractor(outputDir).extractFrames(
                dataSource,
                1000,
                frameSize,
                callback
            )
        }.start()
    }

    var percent: Float = 0f
    var span: Long = 50
    var seconds: Int = 30

    private fun scroll() {
        Thread() {
            var progress: Float = 0f
            while (progress < seconds) {
                progress += (span * 0.001f)
                Thread.sleep(span)
                runOnUiThread {
                    rangeBar.scrollStart(seconds, span)
                }
            }
            T.e("🐶", "progress=" + progress)
        }.start()
    }
}

