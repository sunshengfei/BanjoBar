package io.fuwafuwa.banjo.extension;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

import io.fuwafuwa.banjo.model.Size;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class FFmpegMMR {

    private final String dataSource;
    private final Size frameSize;
    private final VideoThumbNailExtractor.Callback callback;
    private boolean isRunning;

    public FFmpegMMR(String dataSource,
                     Size frameSize,
                     VideoThumbNailExtractor.Callback callback) {
        this.dataSource = dataSource;
        this.frameSize = frameSize;
        this.callback = callback;
    }

    public void start(List<Long> thumbsSlot, String outputDir) {
        FFmpegMediaMetadataRetriever mmr = null;
        isRunning = true;
        try {
            mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(dataSource);
            Long secPoint;
            for (int i = 0; i < thumbsSlot.size(); i++) {
                if (!isRunning) break;
                secPoint = thumbsSlot.get(i);
                Bitmap bitmap = createThumbNailByMMR(mmr, secPoint, frameSize);
                String fileName = "thumb_" + secPoint;
                String urlLocation = outputDir + File.separator + fileName;
                BitmapUtils.saveBitmap(urlLocation, bitmap);
                if (callback != null) {
                    callback.onThumbReceived(i, urlLocation);
//                        callback.onFrameAvailable(bitmap, i);
                }
            }
        } catch (Exception e) {

        } finally {
            if (mmr != null)
                mmr.release();
            isRunning = false;
        }
    }

    private Bitmap createThumbNailByMMR(FFmpegMediaMetadataRetriever mmr, Long secPointMills, Size frameSize) {
        if (frameSize != null) {
            //压缩
            return mmr.getScaledFrameAtTime(secPointMills * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST, frameSize.getWidth(), frameSize.getHeight());
        }
        return mmr.getFrameAtTime(secPointMills * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
    }


    public void release() {
        isRunning = false;
    }

}
