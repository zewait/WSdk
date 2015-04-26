package com.h4fan.wsdk.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.nativecode.ImagePipelineNativeLoader;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.h4fan.lib.utl.ComponentUtil;
import com.h4fan.wsdk.R;
import com.orhanobut.logger.Logger;
import com.polites.android.GestureImageView;

import java.io.IOException;

public class ZoomActivity extends ActionBarActivity {
    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way
     */
    private Animator mCurrentAnimator;
    /**
     * The system "short" animation time duration, in milliseconds.
     * This duration is ideal for subtle animations or animations
     * that occur very frequently
     */
    private int mShortAnimationDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);


        ComponentUtil.generateToolbar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Hook up clicks on the thumbnail views.

        final SimpleDraweeView thumb1View = (SimpleDraweeView) findViewById(R.id.thumb_button_1);
        thumb1View.setImageURI(Uri.parse("http://e.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a7252422a2d343ad4bd113028b.jpg"));
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(thumb1View, "http://e.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a7252422a2d343ad4bd113028b.jpg");
            }
        });

        final SimpleDraweeView thumb2View = (SimpleDraweeView) findViewById(R.id.thumb_button_2);
        thumb2View.setImageURI(Uri.parse("http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e7ba428d034d12f2eb93894bb.jpg"));
        thumb2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(thumb2View, "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e7ba428d034d12f2eb93894bb.jpg");
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * "Zooms" in thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area.
     * More specifically:
     * 1. Assign the high image to the hidden "zoom-in" (expanded) image view.
     * 2. Calculate the starting and ending bounds for the expanded view.
     * 3. Animate each of four positioning/sizing properties(X, Y, SCALE_X, SCALE_Y) simultaneously,
     * , from the starting bounds to the ending bounds.
     * 4. Zoom back out by running the reverse animation on click.
     *
     * @param thumbView The thumbnail view to zoom in.
     * @param url The high-resolution version of the image represented by the thumbnail.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void zoomImageFromThumb(final View thumbView, String url) {
        if(null != mCurrentAnimator) {
            mCurrentAnimator.cancel();
        }
        final GestureImageView existView = (GestureImageView) findViewById(R.id.expand_view);
        final FrameLayout layout = (FrameLayout) findViewById(R.id.container);
        if(null != existView) {
            layout.removeView(existView);
        }

        // Load the high-resolution "zoomed-in" image.
        final GestureImageView expandedImageView = new GestureImageView(this);
        expandedImageView.setId(R.id.expand_view);
        expandedImageView.setMinScale(0.5f);
        expandedImageView.setMaxScale(10.0f);
        expandedImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(expandedImageView);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =  imagePipeline.fetchImageFromBitmapCache(request, null);
        CloseableReference<CloseableImage> imageReference = null;
        try {
            imageReference = dataSource.getResult();
            if(null != imageReference) {
                expandedImageView.setImageBitmap(((CloseableStaticBitmap)imageReference.get()).getUnderlyingBitmap());
            }

        } finally {
            dataSource.close();
            CloseableReference.closeSafely(imageReference);
        }
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container view.
        // Also set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        final float startScale;
        if((float)finalBounds.width()/finalBounds.height()
                > (float)startBounds.width()/finalBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float)startBounds.height()/finalBounds.height();
            float startWidth = startScale*startScale*finalBounds.width();
            float deltaWidth = (startWidth-startBounds.width())/2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float)startBounds.height()/finalBounds.height();
            float startHeight = startScale*finalBounds.height();
            float deltaHeight = (startHeight-startBounds.height())/2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-int view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, SCALE_Y)
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
           .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
           .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
           .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;

            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom backdown to the original bounds
        // and show the thumbnail instead of the expand image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mCurrentAnimator) mCurrentAnimator.cancel();

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }



}
