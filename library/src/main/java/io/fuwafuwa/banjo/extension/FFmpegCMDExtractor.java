package io.fuwafuwa.banjo.extension;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.androidx.ffmpeg.RxFFmpeg;
import io.androidx.ffmpeg.RxFFmpegCommand;
import io.androidx.ffmpeg.RxStat;
import io.fuwafuwa.banjo.model.Size;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FFmpegCMDExtractor {

    private final String dataSource;
    private final Size frameSize;
    private final VideoThumbNailExtractor.Callback callback;

    private boolean isRunning = false;

    public FFmpegCMDExtractor(String dataSource,
                              Size frameSize,
                              VideoThumbNailExtractor.Callback callback) {
        this.dataSource = dataSource;
        this.frameSize = frameSize;
        this.callback = callback;
    }

    private final AtomicInteger currentPos = new AtomicInteger();

    public void start(List<Long> thumbsSlot, String outputDir) {
        isRunning = true;
        for (int i = 0; i < thumbsSlot.size(); i++) {
            if (!isRunning) {
                break;
            }
            Long secPoint = thumbsSlot.get(i);
            String fileName = "thumb_" + secPoint;
            String urlLocation = outputDir + File.separator + fileName;
//            String[] cmds = RxFFmpegCommand.INSTANCE()
//                    .$ffmpeg(true)
//                    .$params("-ss", TMUtils.$timeString(secPoint))
//                    .$input(dataSource)
//                    .$params("-f", "image2", "-vframes", "1")
//                    .$params("-s", frameSize.getWidth() + "x" + frameSize.getHeight())
//                    .$output(urlLocation)
//                    .build();
            String[] cmds = RxFFmpegCommand.INSTANCE()
                    .$ffmpeg(true)
                    .$params("-ss", TMUtils.$timeString(secPoint))
                    .$input(dataSource)
                    .$params("-f", "image2", "-vframes", "1")
                    .$params("-s", frameSize.getWidth() + "x" + frameSize.getHeight())
                    .$output(urlLocation)
                    .build();
            synchronized (currentPos) {
                RxFFmpeg.executeCommandWithTag(cmds, i)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(rxStat -> {
                            synchronized (currentPos) {
                                if (rxStat.getStatus() == RxStat.Status.STATUS_COMPLETE) {
                                    currentPos.notify();
                                    if (callback != null) {
                                        callback.onThumbReceived(rxStat.getTag(), urlLocation);
                                    }
                                }
                            }
                        }, e -> {
                            synchronized (currentPos) {
                                currentPos.notify();
                            }
                        });
                try {
                    currentPos.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release() {
        isRunning = false;
        synchronized (currentPos) {
            currentPos.notify();
        }
        RxFFmpeg.cancelTask();
    }
}
