package com.santiagogil.takestock.view.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.ItemList;

import java.util.List;

public class ListRecyclerAdapter extends RecyclerView.Adapter {

    private List<ItemList> itemLists;
    private Context context;
    private View.OnClickListener onItemTouchedListener;

    public ListRecyclerAdapter(List<ItemList> itemList, Context context, View.OnClickListener onItemTouchedListener) {
        this.itemLists = itemList;
        this.context = context;
        this.onItemTouchedListener = onItemTouchedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_item_list, parent, false);
        view.setOnClickListener(onItemTouchedListener);
        return new ItemListViewHolder(view, context, onItemTouchedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemList itemList = itemLists.get(position);
        ItemListViewHolder itemListViewHolder = (ItemListViewHolder) holder;
        itemListViewHolder.loadItemList(itemList);

    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    private class ItemListViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewItemListName;

        public ItemListViewHolder(View itemView, Context context, View.OnClickListener onItemTouchedListener) {
            super(itemView);

            itemView.setOnClickListener(onItemTouchedListener);
            textViewItemListName = itemView.findViewById(R.id.text_view_item_list_name);
        }

        public void loadItemList(ItemList itemList){

            textViewItemListName.setText(itemList.getName());

        }
    }
}
