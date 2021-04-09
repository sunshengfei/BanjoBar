package io.fuwafuwa.banjo.test

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.androidx.ffmpeg.RxProbe
import io.fuwafuwa.banjo.*
import io.fuwafuwa.banjo.extension.Frame
import io.fuwafuwa.banjo.extension.MediaUtils
import io.fuwafuwa.banjo.extension.T
import io.fuwafuwa.banjo.model.Segment
import io.fuwafuwa.banjo.model.Size
import io.fuwafuwa.banjo.profile.SegmentConfig
import io.fuwafuwa.banjo.profile.TrackConfig
import io.fuwafuwa.banjo.ui.ThumbNaiUnit
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_banjo.*
import java.io.File
import java.util.*

class BanjoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banjo)
        Frame.init(windowManager)

        val mThumbActionListener: OnThumbActionListener = object : OnThumbActionListener {
            override fun onSegmentChange(
                thumbSegmentProvider: IThumbHandlerProvider<View>?,
                segment: Segment?
            ) {

            }

            override fun onDataSetChanged(list: MutableList<ThumbNaiUnit>?) {

            }

            override fun onFocused(provider: IThumbHandlerProvider<View>) {}
            override fun onUnFocused(provider: IThumbHandlerProvider<View>) {}
            override fun onDragStart(
                provider: IThumbHandlerProvider<View>,
                isLeft: Boolean
            ) {


            }

            override fun onDragEnd(
                provider: IThumbHandlerProvider<View>,
                isLeft: Boolean
            ) {
//                clip_start_time.text = provider.segment?.from.toString()
//                clip_end_time.text = provider.segment?.to.toString()
//                T.e("Segment", provider.axisRange().toString())
            }

            override fun onDragging(
                provider: IThumbHandlerProvider<View>,
                isLeft: Boolean
            ) {
                clip_start_time.text = provider.segment?.from.toString()
                clip_end_time.text = provider.segment?.to.toString()
                T.e("Segment", provider.axisRange().toString())
            }

            override fun onSelection(
                thumbSegmentProvider: IThumbHandlerProvider<View>,
                segment: Segment
            ) {

            }

            override fun onSegmentAdded(thumbView: IThumbHandlerProvider<View>?) {

            }

            override fun onSelectionCheckedChange(
                thumbSegmentProvider: IThumbHandlerProvider<View>,
                segment: Segment?,
                checked: Boolean
            ) {
                if (checked)
                    Toast.makeText(
                        this@BanjoActivity,
                        "点击了组ID:${thumbSegmentProvider.groupId}，元素id${segment?.id.toString()},区间[${segment?.from.toString()},${segment?.to.toString()}]",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            override fun onSegmentRemoved(
                thumbView: IThumbHandlerProvider<View>?
            ) {

            }
        }

        rangeBar2.setThumbActionListener(mThumbActionListener)

        rangeBar2.setSelectionBarStateEvent { contentLength ->
            T.e("🍑", "contentLength=${contentLength}")
        }

        buttonTest2.setOnClickListener {
            startBanjoClip()
        }

        buttonTest21.setOnClickListener {
            addThumb()
        }
        buttonTest212.setOnClickListener {
            val trackConfig = TrackConfig()
            trackConfig.trackBackground = ContextCompat.getDrawable(this, R.drawable.wave)
            rangeBar2.addThumbGroup(trackConfig)
        }

        buttonTest23.setOnCheckedChangeListener { buttonView, isChecked ->
            config.layoutMode =
                if (isChecked) ThumbDefaultGlobalSettings.LayoutMode.EXPAND_AFTER else ThumbDefaultGlobalSettings.LayoutMode.OVERLAY
            rangeBar2.updateConfig(config)
        }
        buttonTest211.setOnCheckedChangeListener { buttonView, isChecked ->
            config.isScrollableWhenLess = isChecked
            rangeBar2.updateConfig(config)
        }
        path.setText(dataSource2)
        buttonTest3.setOnClickListener {
            var file = path.text.toString()
            RxProbe.getMediaDetail(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { res: String? ->
                    if (res == null) {
                        result.text = "Empty"
                        return@subscribe
                    }
                    result.text = res
                }
        }
        config = ThumbDefaultGlobalSettings.Config()
        config.isScrollableWhenLess = false
        config.isShowIndicator = false
        config.thumbNailSize = frameSize
        config.progressBarColor = 0x800044FF.toInt()
//        config.layoutMode = ThumbDefaultGlobalSettings.LayoutMode.EXPAND_AFTER
//        config.preferThumbExtractor = ExtractHandler.MMR
    }

    private fun addThumb() {
        var list = rangeBar2.trackList
        if (list == null || list.size == 0) return
        val index = list.size - 1
        var segment = Segment();
        segment.from = 0f
        segment.to = -1f
        val cfg = SegmentConfig()
        cfg.selectedColor = 0x800000FF.toInt()
//            cfg.justify = BarJustify.FIXED
//          cfg.maskBackground = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)
//          if (index == 1) {
//              cfg.maskBackground = ContextCompat.getDrawable(this, R.drawable.wave)
//          }
        rangeBar2.addThumb(segment, index, cfg)
    }

    private lateinit var config: ThumbDefaultGlobalSettings.Config
    val list: MutableList<ThumbNaiUnit> = ArrayList()

    var dataSource2 = "/sdcard/Movies/1998-Bravo-AllStars-Let-The-Music-Heal-Your-Soul.mp4"

    //    var dataSource = "/sdcard/Movies/d.1.mp4"
//    var dataSource = "/sdcard/Movies/ddd.mp4"
    var dataSource = "/sdcard/Movies/A_10_1.mp4"

    //    var dataSource = "/sdcard/Movies/1111.mp4"
    var frameSize = Size(90, 120)

    private fun startBanjoClip() {
        var videoName = File(dataSource).name
        var outputDir = getExternalFilesDir("")?.absolutePath + File.separator + videoName
        var duration = MediaUtils.getDuration(dataSource)

        val frameSize = Size(90, 120)
        rangeBar2.updateConfig(config)
        val inset: Int = Frame.dp2px(16f) * 2 //formPadding">
        val handlerWidth = config.handlerWidth
        val distance: Int = Frame.getScreenWidth() - inset - handlerWidth.toInt() * 2
        //一屏显示完,最多显示Thumb个数
        val count = distance / frameSize.width
        var spanMills = 0L
        if (count > 0) {
            spanMills = (duration / count)
        } else {
            spanMills = 500
        }
//        VideoThumbNailExtractor.useFastMode = false
        rangeBar2.attachVideoSource(
            dataSource,
            outputDir,
            spanMills, frameSize
        )
//        rangeBar2.postDelayed(Runnable {
//            rangeBar2.addThumb(0, -1)
////            rangeBar.addThumb()
//        }, 200)
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
//                    rangeBar.scrollStart(seconds, span)
                }
            }
            T.e("🐶", "progress=" + progress)
        }.start()
//        rangeBar.postDelayed({
//            rangeBar.scrollStart(seconds, span)
//            scroll()
//        }, span)
    }
}

