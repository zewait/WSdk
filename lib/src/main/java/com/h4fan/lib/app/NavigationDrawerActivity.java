package com.h4fan.lib.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.h4fan.lib.R;

import cn.trinea.android.common.util.PreferencesUtils;

/**
 * Created by shifanhuang on 14/4/19.
 */
public class NavigationDrawerActivity extends ActionBarActivity {
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private boolean mUserLearnedDrawer = false;
    private boolean mFromSavedInstanceState = false;

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected View mLeftDrawerView;

    public final void setLeftDrawerView(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.left_drawer_view, f, "left_drawer")
                .commit();
    }

    public final void setContent(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, f, "content")
                .commit();
    }

    public final void pushToContent(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, f, "content")
                .addToBackStack("content")
                .commit();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_navigation_drawer);

        if(null!=savedInstanceState) {
            mFromSavedInstanceState = true;
        }

        injectView();

        mUserLearnedDrawer = PreferencesUtils.getBoolean(this, PREF_USER_LEARNED_DRAWER, false);

        setSupportActionBar(mToolbar);
        // open up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawer();
    }

    private final void setUpDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_close, R.string.drawer_open) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    PreferencesUtils.putBoolean(NavigationDrawerActivity.this, PREF_USER_LEARNED_DRAWER, mUserLearnedDrawer);
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if(!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mLeftDrawerView);
        }

        mDrawerToggle.syncState();
    }

    private final void injectView() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawerView = findViewById(R.id.left_drawer_view);
    }


}
