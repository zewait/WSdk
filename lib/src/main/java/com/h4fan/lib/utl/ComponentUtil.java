package com.h4fan.lib.utl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

import com.h4fan.lib.R;
import com.h4fan.lib.entity.Screen;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * 组件缩放工具
 *
 * @author 黄世凡
 * @version 1.0
 * @since 2013年12月25日 下午3:39:04
 */
public class ComponentUtil {
    private ComponentUtil() {
        throw new AssertionError();
    }
    /**
     * 将原视图 宽高，padding，margin, 及文本字体大小 按比例缩放，重新布局；
     *
     * @param context
     * @param layoutResID
     * @return
     */
    public final static View scaleViewHierarchy(Context context, int layoutResID) {
        View view = View.inflate(context, layoutResID, null);
        return scaleViewHierarchy(view, Screen.getScale());

    }

    /**
     * 将原视图 宽高，padding，margin, 及文本字体大小 按比例缩放，重新布局；
     * 注意:前提要设置了scale
     *
     * @param view
     */
    public final static View scaleViewHierarchy(View view) {
        return scaleViewHierarchy(view, Screen.getScale());
    }

    /**
     * 将原视图 宽高，padding，margin, 及文本字体大小 按比例缩放，重新布局；
     *
     * @param view  单个视图，或视图层级
     * @param scale 缩放比例
     */
    public final static View scaleViewHierarchy(View view, float scale) {

        if (view == null) {
            return null;
        }
        scaleView(view, scale);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroupTemp = (ViewGroup) view;
            if (viewGroupTemp.getChildCount() > 0) {
                for (int i = 0; i < viewGroupTemp.getChildCount(); i++) {
                    scaleViewHierarchy(viewGroupTemp.getChildAt(i), scale);
                }
            }
        }
        return view;
    }

    /**
     * 将视图按比例缩放，不考虑嵌套视图；
     *
     * @param view  不考虑嵌套，缩放单个View；
     * @param scale 缩放比例；
     */
    private final static void scaleView(View view, float scale) {
        if (view instanceof TextView) {
            resetTextSize((TextView) view, scale);
        }
        int pLeft = convertFloatToInt(view.getPaddingLeft() * scale);
        int pTop = convertFloatToInt(view.getPaddingTop() * scale);
        int pRight = convertFloatToInt(view.getPaddingRight() * scale);
        int pBottom = convertFloatToInt(view.getPaddingBottom() * scale);
        view.setPadding(pLeft, pTop, pRight, pBottom);

        scaleLayoutParams(view, scale);
    }

    /**
     * 将视图布局属性按比例设置；
     *
     * @param view
     * @param scale 缩放比例；
     */
    public final static void scaleLayoutParams(View view, float scale) {
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }

        if (params.width > 0) {
            params.width = convertFloatToInt(params.width * scale);
        }
//		else if(LayoutParams.WRAP_CONTENT == params.width)
//		{
//			//木有指定宽度,通过计算获取他的宽度
//			params.width = convertFloatToInt(MeasureUtil.getWidth(view) * scale);
//		}
        if (params.height > 0) {
            params.height = convertFloatToInt(params.height * scale);
        }
//		else if(LayoutParams.WRAP_CONTENT == params.height)
//		{
//			params.height = convertFloatToInt(MeasureUtil.getHeight(view) * scale);
//		}
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams mParams = (MarginLayoutParams) params;
            if (mParams.leftMargin > 0) {
                mParams.leftMargin = convertFloatToInt(mParams.leftMargin
                        * scale);
            }
            if (mParams.rightMargin > 0) {
                mParams.rightMargin = convertFloatToInt(mParams.rightMargin
                        * scale);
            }
            if (mParams.topMargin > 0) {
                mParams.topMargin = convertFloatToInt(mParams.topMargin * scale);
            }
            if (mParams.bottomMargin > 0) {
                mParams.bottomMargin = convertFloatToInt(mParams.bottomMargin
                        * scale);
            }
        }
    }

    /**
     * 将 TextView（或其子类）文本大小按比例缩放；
     *
     * @param textView
     * @param scale    缩放比例；
     */
    private final static void resetTextSize(TextView textView, float scale) {
        float size = textView.getTextSize();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale);
    }

    /**
     * float 转换至 int 小数四舍五入
     */
    public final static int convertFloatToInt(float sourceNum) {
        return (int) (sourceNum + 0.5f);
    }

    /**
     * init app bar when layout include @layout/appbar, otherwise throw Exception
     *
     * @param activity
     * @return
     */
    public final static Toolbar generateToolbar(ActionBarActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.app_bar);
        activity.setSupportActionBar(toolbar);
        return toolbar;
    }

    public final static PtrUIHandler generateMaterialPullRefresh(PtrFrameLayout layout, @Nullable PtrHandler handler) {
        final MaterialHeader header = new MaterialHeader(layout.getContext());
        int[] colors = layout.getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        header.setPtrFrameLayout(layout);


        layout.addPtrUIHandler(header);
        layout.setHeaderView(header);
        if(null != handler)
            layout.setPtrHandler(handler);

        return header;
    }


}
