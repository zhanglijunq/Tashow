package com.showjoy.tashow.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.showjoy.tashow.R;

/**
 * Created by mac on 16/5/17.
 */
public class SpanUtils {

    public static SpannableString spanColor(Context context, String content,int start,int end,int color,int size,boolean isBold){
        SpannableString spanString = new SpannableString(content);
        ForegroundColorSpan fspan = new ForegroundColorSpan(context.getResources().getColor(color));
        AbsoluteSizeSpan aspan = new AbsoluteSizeSpan(size);
        if (isBold){
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);//加粗
            spanString.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spanString.setSpan(fspan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(aspan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return  spanString;
    }

}
