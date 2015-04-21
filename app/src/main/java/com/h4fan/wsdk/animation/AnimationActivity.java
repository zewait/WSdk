package com.h4fan.wsdk.animation;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.h4fan.lib.utl.ComponentUtil;
import com.h4fan.wsdk.R;

import java.util.ArrayList;
import java.util.List;

public class AnimationActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView mList;
    private List<Sample> mSamples = new ArrayList<Sample>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ComponentUtil.generateToolbar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        injectView();

        mSamples.add(new Sample("ZoomView", ZoomActivity.class));

        mList.setAdapter(new ArrayAdapter<Sample>(this, android.R.layout.simple_list_item_1, mSamples));
        mList.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, mSamples.get(i).activityClasses));

    }

    private final void injectView() {
        mList = (ListView) findViewById(R.id.list);
    }

    private class Sample {
        private CharSequence title;
        private Class<? extends Activity> activityClasses;

        Sample(CharSequence title, Class<? extends Activity> activityClasses) {
            this.title = title;
            this.activityClasses = activityClasses;
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }
}
