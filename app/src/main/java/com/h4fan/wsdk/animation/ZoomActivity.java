package com.h4fan.wsdk.animation;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.h4fan.lib.utl.ComponentUtil;
import com.h4fan.wsdk.R;

public class ZoomActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        ComponentUtil.generateToolbar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
