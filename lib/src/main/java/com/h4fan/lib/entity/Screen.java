package com.h4fan.lib.entity;

import android.content.Context;

/**
 * Created by shifanhuang on 15/4/17.
 *
 * design screen
 */
public class Screen {
    private static int width = 320;
    private static int height = 480;
    private static int designWidth = 320;
    private static int designHeight = 480;
    private static float scaleWidth = width/(float)designHeight;
    private static float scaleHeight = height/(float)designHeight;
    /**
     * is it adapter view by width?
     */
    private static boolean isAdapterWidth = true;


    private Screen() {
        throw new AssertionError();
    }

    public static final void init(Context context, int designWidth, int designHeight, boolean isAdapterWidth) {
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        Screen.designWidth = designWidth;
        Screen.designHeight = designHeight;
        Screen.isAdapterWidth = isAdapterWidth;

        scaleWidth = width/(float)designWidth;
        scaleHeight = height/(float)designHeight;
    }


    public static final int getWidth() {
        return width;
    }

    public static final int getHeight() {
        return height;
    }

    public static final int getDesignWidth() {
        return designWidth;
    }

    public static final int getDesignHeight() {
        return designHeight;
    }

    public static final boolean isAdapterWidth() {
        return isAdapterWidth;
    }

    public static final float getScaleHeight() {
        return scaleHeight;
    }

    public static final float getScaleWidth() {
        return scaleWidth;
    }

    public static final float getScale() {
        return isAdapterWidth ? scaleWidth : scaleHeight;
    }
}
