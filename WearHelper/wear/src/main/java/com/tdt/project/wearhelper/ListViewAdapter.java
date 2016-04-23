package com.tdt.project.wearhelper;

import android.content.Context;
import android.content.Intent;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;

import java.util.List;

public class ListViewAdapter extends WearableListView.Adapter  {

    private Context context;
    private List<ListViewItem> listViewItems;

    public ListViewAdapter(Context context, List<ListViewItem> listViewItems) {
        this.context = context;
        this.listViewItems = listViewItems;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(new ListViewRowView(context));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
        ListViewRowView listViewRowView = (ListViewRowView) viewHolder.itemView;
        final ListViewItem listViewItem = listViewItems.get(i);

        listViewRowView.getImage().setImageResource(listViewItem.getImageRes());
        listViewRowView.getText().setText(listViewItem.getText());
    }

    @Override
    public int getItemCount() {
        return listViewItems.size();
    }
}
