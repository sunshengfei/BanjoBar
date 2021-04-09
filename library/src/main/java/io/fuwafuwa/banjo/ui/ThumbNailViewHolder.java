package io.fuwafuwa.banjo.ui;

import android.view.ViewGroup;
import android.widget.ImageView;

import io.fuwafuwa.banjo.extension.ImageLoader;
import io.fuwafuwa.banjo.R;
import io.fuwafuwa.banjo.model.Size;

public
class ThumbNailViewHolder extends DefaultRecyclerViewHolder<ThumbNaiUnit> {

    private Size thumbSize;
    private ImageView imageView;

    public ThumbNailViewHolder(ViewGroup parent, Size thumbSize) {
        super(parent, R.layout.thumb_item);
        this.thumbSize = thumbSize;
        imageView = $(R.id.thumb_image);
        if (thumbSize != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = thumbSize.getWidth();
            params.height = thumbSize.getHeight();
        }
    }


    @Override
    public void update(ThumbNaiUnit data) {
        super.update(data);
        ImageLoader.newInstance().loadImage(mContextRef.get(), data.getThumbUrl(), imageView);
    }
}
