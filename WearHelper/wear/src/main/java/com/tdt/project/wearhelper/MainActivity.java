package com.tdt.project.wearhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener, WearableListView.OnScrollListener {

    private List<ListViewItem> viewItemList;
    private TextView mHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_activity);

        viewItemList = new ArrayList<>();
        mHead = (TextView) findViewById(R.id.header);
        WearableListView wearableListView = (WearableListView) findViewById(R.id.wearable_list_view);

        //setup item selection
        viewItemList.add(new ListViewItem(R.drawable.connect, getString(R.string.batterystatus), new Intent(this, BatteryStatusActivity.class)));
        viewItemList.add(new ListViewItem(R.drawable.findmyphone, getString(R.string.findmyphone), new Intent(this, FindPhoneActivity.class)));

        wearableListView.setAdapter(new ListViewAdapter(this, viewItemList));
        wearableListView.setClickListener(this);
        wearableListView.addOnScrollListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //call activity
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        int position = viewHolder.getPosition();
        ListViewItem item = viewItemList.get(position);
        startActivity(item.getIntent());
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onScroll(int i) {

    }

    @Override
    public void onAbsoluteScrollChange(int i) {
        if (i > 0) {
            mHead.setY(-i);
        }
    }

    @Override
    public void onScrollStateChanged(int i) {

    }

    @Override
    public void onCentralPositionChanged(int i) {

    }
}