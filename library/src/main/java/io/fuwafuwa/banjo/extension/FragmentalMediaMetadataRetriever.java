package io.fuwafuwa.banjo.extension;

import android.media.MediaMetadataRetriever;

public class FragmentalMediaMetadataRetriever extends MediaMetadataRetriever implements AutoCloseable {

    @Override
    public void close() {
        release();
    }
}
