package com.codepath.apps.simpletwitter.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Samn on 02-Jul-17.
 */

public class ResourceUtils {

    public static void loadImageCircle(Context context, ImageView imageView, String url){
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(imageView);
    }

    public static void loadImageRoundedCorner(Context context, ImageView imageView, String url){
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 1))
                .into(imageView);
    }
}
