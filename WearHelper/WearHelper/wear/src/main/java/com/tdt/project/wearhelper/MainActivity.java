package com.tdt.project.wearhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener, WearableListView.OnScrollListener {

    public boolean mLangagues = false;

    private List<ListViewItem> viewItemList;
    private TextView mHead;
    String itemStatus;
    String itemFind;
    String itemSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_activity);

        viewItemList = new ArrayList<>();
        mHead = (TextView) findViewById(R.id.header);
        WearableListView wearableListView = (WearableListView) findViewById(R.id.wearable_list_view);

        itemStatus = "Battery Status";
        itemFind = "Find My Phone";
        itemSetting = "Setting";

        //setup item selection
        viewItemList.add(new ListViewItem(R.drawable.connect, itemStatus, new Intent(this, PhoneStatusActivity.class)));
        viewItemList.add(new ListViewItem(R.drawable.findmyphone, itemFind, new Intent(this, FindPhoneActivity.class)));
        viewItemList.add(new ListViewItem(R.drawable.setting, itemSetting, new Intent(this, SettingActivity.class)));

        wearableListView.setAdapter(new ListViewAdapter(this, viewItemList));
        wearableListView.setClickListener(this);
        wearableListView.addOnScrollListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLangagues) {
            itemStatus = "Tình trạng PIN";
            itemFind = "Tìm điện thoại";
            itemSetting = "Cài đặt";
        }
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