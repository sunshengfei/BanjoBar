package io.fuwafuwa.banjo.extension;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;


/**
 * Created by fred on 2016/11/14.
 */

public class ImageLoader {

    private static final String SEPARATOR = File.separator;
    private static ImageLoader imageLoader;
    private Drawable errorDrawable;


    public static ImageLoader newInstance() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    public ImageLoader error(Drawable drawable) {
        imageLoader.errorDrawable = drawable;
        return imageLoader;
    }

    public void loadImage(@NonNull Context context, String urlString, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        Glide.with(context).load(urlStringFilter(urlString))
//                .placeholder(R.drawable.loading_holder)
                .error(imageLoader.errorDrawable)
                .dontAnimate()
                .into(iv);
    }

    public void loadImageWithAppIcon(@NonNull Context context, String urlString, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        Glide.with(context)
                .load(urlStringFilter(urlString))
                .error(imageLoader.errorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(iv);
    }


    public void loadImage(Drawable drawable, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        iv.setImageDrawable(drawable);
    }

    public void loadImage(@NonNull Activity activity, String urlString, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        Glide.with(activity).load(urlStringFilter(urlString))
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }


    public void loadImageRaw(@NonNull Activity activity, String urlString, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        Glide.with(activity).load(urlString)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);
    }

    public void loadImage(@NonNull Fragment fragment, String urlString, @NonNull ImageView iv) {
        if (imageLoader == null) return;
        Glide.with(fragment).load(urlStringFilter(urlString))
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);
    }


    public static String urlStringFilter(String urlString) {
        if (urlString != null) {
            String url = urlString.toLowerCase();
            if (url.startsWith("file:") ||
                    url.startsWith("content:") ||
                    url.startsWith("http")) {
                return urlString;
            }
            if (!urlString.startsWith("//")) {
                if (urlString.startsWith(SEPARATOR)) {
                    return getHost() + urlString.replaceFirst("[/]", "");
                } else {
                    return getHost() + urlString;
                }
            }
        }
        return urlString;
    }

    private static String getHost() {
        return "";
    }

}
