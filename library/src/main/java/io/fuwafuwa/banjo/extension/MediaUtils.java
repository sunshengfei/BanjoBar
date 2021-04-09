package io.fuwafuwa.banjo.extension;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import io.fuwafuwa.banjo.model.Size;

public class MediaUtils {

    /**
     * 文件格式如果不支持，将始终0
     *
     * @param mContext
     * @param currentDataSource
     * @return
     */
    public static long getDuration(Context mContext, Uri currentDataSource) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(mContext, currentDataSource);
        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = 0;
        try {
            duration = Long.parseLong(durationStr);
        } catch (NumberFormatException e) {
        } finally {
            metadataRetriever.release();
        }
        return duration;
    }

    public static long getDuration(String currentDataSource) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(currentDataSource);
        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = 0;
        try {
            duration = Long.parseLong(durationStr);
        } catch (NumberFormatException e) {
        } finally {
            metadataRetriever.release();
        }
        return duration;
    }


    public static MediaInfo getMediaInfo(String currentDataSource) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(currentDataSource);
        String oWidth = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String oHeight = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String rotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        MediaInfo mediaInfo = new MediaInfo();
        Size size = null;
        try {
            int width = 0, height = 0;
            if (RegexHelper.isNatureSerialNumber(oWidth)) {
                width = Integer.parseInt(oWidth);
            }
            if (RegexHelper.isNatureSerialNumber(oHeight)) {
                height = Integer.parseInt(oHeight);
            }
            if (width != 0 && height != 0) {
                size = new Size(width, height);
                mediaInfo.setSize(size);
            }
            if (RegexHelper.isNatureSerialNumber(duration)) {
                int durationInt = Integer.parseInt(duration);
                mediaInfo.setDuration(durationInt);
            }
            if (RegexHelper.isNatureSerialNumber(rotation)) {
                int rotationInt = Integer.parseInt(rotation);
                mediaInfo.setRotation(rotationInt);
            }
        } finally {
            metadataRetriever.release();
        }
        return mediaInfo;
    }

}
