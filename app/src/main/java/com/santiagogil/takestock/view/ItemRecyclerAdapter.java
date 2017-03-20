package com.santiagogil.takestock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter{

    private List<Item> items;
    private Context context;
    private View.OnClickListener onStockModifiedListener;
    private View.OnClickListener onItemTouchedListener;

    public List<Item> getItems() {
        return items;
    }

    public ItemRecyclerAdapter(Context context, View.OnClickListener onStockModifiedListener, View.OnClickListener onItemTouchedListener) {
        this.context = context;
        this.onStockModifiedListener = onStockModifiedListener;
        this.onItemTouchedListener = onItemTouchedListener;
        this.items = new ArrayList<>();

    }

    public Item getItemAtPosition(Integer position){return items.get(position);}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_item, parent, false);
        view.setOnClickListener(onItemTouchedListener);
        return new ItemViewHolder(view, context, onStockModifiedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.loadItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewItemName;
        private TextView textViewStock;
        private TextView textViewIndependence;
        private Button buttonSubtract;
        private Button buttonAdd;
        private Context context;
        private View.OnClickListener onStockModifiedListener;
        private View itemView;

        public ItemViewHolder(View itemView, Context context, View.OnClickListener onStockModifiedListener) {
            super(itemView);

            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewStock = (TextView) itemView.findViewById(R.id.textViewStock);
            textViewIndependence = (TextView) itemView.findViewById(R.id.textViewIndependence);

            buttonAdd = (Button) itemView.findViewById(R.id.buttonAdd);
            buttonSubtract = (Button) itemView.findViewById(R.id.buttonSubtract);
            this.context = context;
            this.onStockModifiedListener = onStockModifiedListener;
            this.itemView = itemView;
        }

        public void loadItem(final Item item){

            textViewItemName.setText(item.getName());
            String itemStock = item.getStock().toString();
            textViewStock.setText(itemStock);
            textViewIndependence.setText("Independence: " + Math.round(item.getConsumptionRate()*item.getStock()) + " days");
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseItemStock(item);
                    onStockModifiedListener.onClick(itemView);
                }
            });
            buttonAdd.setText("+" + item.getMinimumPurchaceQuantity());
            buttonSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    decreaseItemStock(item);
                    //textViewIndependence.setText(item.getIndependence().toString());
                    onStockModifiedListener.onClick(itemView);
                }
            });

        }

        public  void increaseItemStock(Item item){

            ItemsController itemsController = new ItemsController();
            itemsController.increaseItemStock(context, item);

        }

        public void decreaseItemStock(Item item){

            if(item.getStock() == 0){
                Toast.makeText(context, "Nothing left to consume", Toast.LENGTH_SHORT).show();
            } else {

                ItemsController itemsController = new ItemsController();
                itemsController.decreaseItemStock(context, item);
                ConsumptionsController consumptionsController = new ConsumptionsController();
                consumptionsController.addConsumptionToDatabases(context, item.getID());
                itemsController.updateItemConsumptionRate(context, item.getID());
            }
        }
    }
}
