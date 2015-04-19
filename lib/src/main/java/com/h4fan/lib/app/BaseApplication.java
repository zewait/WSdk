package com.h4fan.lib.app;

import android.app.Application;
import android.content.Context;
import android.view.ViewConfiguration;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.h4fan.lib.entity.Screen;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

/**
 * Created by shifanhuang on 15/4/17.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setHasPermanentMenuKey(false);

        initFresco();

        Logger.init("WSdk")
                .hideThreadInfo()
                .setLogLevel(LogLevel.FULL);
        Screen.init(this, 320, 480, true);
    }


    public final void setHasPermanentMenuKey(boolean flag) {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(null != menuKeyField) {
                menuKeyField.setAccessible(true);
                menuKeyField.set(config, flag);
            }
        } catch(Exception e) {
            // Ignore
        }
    }

    private final void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig
                .newBuilder(this)
                .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
                    @Override
                    public int getNextScanNumberToDecode(int i) {
                        return i+2;
                    }

                    @Override
                    public QualityInfo getQualityInfo(int i) {
                        boolean isGoodEnough = i>=5;
                        return ImmutableQualityInfo.of(i, isGoodEnough, false);
                    }
                })
                .build();
        Fresco.initialize(this, config);
    }
}
