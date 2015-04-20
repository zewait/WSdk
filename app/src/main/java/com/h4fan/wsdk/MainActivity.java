package com.h4fan.wsdk;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.h4fan.lib.app.NavigationDrawerActivity;
import com.h4fan.lib.utl.ComponentUtil;
import com.h4fan.wsdk.demo.fragmentbackstack.FragmentA;
import com.h4fan.wsdk.dummy.DummyContent;
import com.orhanobut.logger.Logger;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MainActivity extends NavigationDrawerActivity implements LeftDrawerFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLeftDrawerView(LeftDrawerFragment.newInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(String id) {
        mDrawerLayout.closeDrawer(mLeftDrawerView);
        if(DummyContent.FRAGMENT_BACK_STACK.equals(id)) {
            pushToContent(FragmentA.newInstance(null, null));
            return;
        }
    }

    private final void init() {
        final PtrFrameLayout fl = (PtrFrameLayout) findViewById(R.id.ptr_frame);
        ComponentUtil.generateMaterialPullRefresh(fl, new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                fl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fl.refreshComplete();
                    }
                }, 5000);
            }
        });


        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                Logger.d("final image set");
            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                Logger.d("intermediate: " + id);
            }
        };
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setCornersRadius(100);
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        draweeView.setAspectRatio(1f);
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse("http://192.168.1.2:8080/test/images/1.jpg"))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController ctrl = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .setOldController(draweeView.getController()).build();
        draweeView.setController(ctrl);
    }

}
