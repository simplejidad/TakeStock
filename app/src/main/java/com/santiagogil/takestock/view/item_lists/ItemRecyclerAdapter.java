package com.santiagogil.takestock.view.item_lists;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.view.item_detail.FragmentItemDetail;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter{

    private List<Item> items;
    private Context context;
    private View.OnClickListener onItemModifiedListener;
    private View.OnClickListener onItemTouchedListener;

    public List<Item> getItems() {
        return items;
    }

    public ItemRecyclerAdapter(Context context, View.OnClickListener onItemModifiedListener, View.OnClickListener onItemTouchedListener) {
        this.context = context;
        this.onItemModifiedListener = onItemModifiedListener;
        this.onItemTouchedListener = onItemTouchedListener;
        this.items = new ArrayList<>();

    }

    public Item getItemAtPosition(Integer position){return items.get(position);}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_item, parent, false);
        view.setOnClickListener(onItemTouchedListener);
        return new ItemViewHolder(view, context, onItemModifiedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.loadItem(item);
        itemViewHolder.assignTransitionNames(item.getID());

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
        private TextView textViewItemStock;
        private TextView textViewItemIndependence;
        private TextView textViewItemPrice;
        private TextView textViewNeededForGoal;
        private Button buttonStockSubtract;
        private Button buttonStockAdd;
        private Button buttonCartToStock;
        private ImageButton buttonCartSubtract;
        private ImageButton buttonCartAdd;
        private Context context;
        private View.OnClickListener onItemModifiedListener;
        private View itemView;

        public ItemViewHolder(View itemView, Context context, View.OnClickListener onItemModifiedListener) {
            super(itemView);

            textViewItemName = (TextView) itemView.findViewById(R.id.text_view_item_name);
            textViewItemStock = (TextView) itemView.findViewById(R.id.text_view_item_stock);
            textViewItemIndependence = (TextView) itemView.findViewById(R.id.text_view_item_independence);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.text_view_item_price);
            textViewNeededForGoal = (TextView) itemView.findViewById(R.id.text_view_needed_for_goal);
            buttonStockAdd = (Button) itemView.findViewById(R.id.buttonAdd);
            buttonStockSubtract = (Button) itemView.findViewById(R.id.buttonSubtract);
            buttonCartSubtract = (ImageButton) itemView.findViewById(R.id.button_cart_subtract);
            buttonCartAdd = (ImageButton) itemView.findViewById(R.id.button_cart_add);
            buttonCartToStock = (Button) itemView.findViewById(R.id.button_cart_to_stock);
            this.context = context;
            this.onItemModifiedListener = onItemModifiedListener;
            this.itemView = itemView;
        }

        public void loadItem(final Item item){

            String itemStock = item.getStock().toString();
            textViewItemName.setText(item.getName());
            textViewItemStock.setText(itemStock);
            //textViewItemIndependence.setText(Math.round(item.getConsumptionRate()*item.getStock()) + " days");
            setTextViewItemIndependenceText(item);
            setTextViewNeededForGoal(item);
            textViewItemPrice.setText("" + item.getPrice());

            buttonStockAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseItemStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });
            buttonStockAdd.setText("+" + item.getMinimumPurchaceQuantity());
            buttonStockSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    decreaseItemStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });
            buttonCartAdd.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    increaseCart(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });
            buttonCartToStock.setText("<" + item.getCart());
            buttonCartSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseItemCart(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });
            buttonCartToStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartToStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });

        }

        private void setTextViewNeededForGoal(Item item) {

            if(item.getConsumptionRate() > 0){

                Integer independence = item.getIndependence();

                if(independence >= 365){
                    textViewNeededForGoal.setText("");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.emoticon_excited , 0 , 0, 0);
                }
                else if (independence > 90 ){
                    textViewNeededForGoal.setText(round((365-independence)/item.getConsumptionRate(), 0).intValue() + " till ");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0 , R.drawable.emoticon_excited, 0);
                } else if(independence > 30){
                    textViewNeededForGoal.setText("" +  round((90-independence)/item.getConsumptionRate(), 0).intValue()  + " till ");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, R.drawable.ic_insert_emoticon_black_24dp, 0);
                } else if (independence > 7) {
                    textViewNeededForGoal.setText("" + round((30-independence)/item.getConsumptionRate(), 0).intValue()  + " till ");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, R.drawable.ic_insert_emoticon_black_24dp, 0);
                }else if (independence >= 0) {
                    textViewNeededForGoal.setText("" + round((7-independence)/item.getConsumptionRate(), 0).intValue()  + " till ");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, R.drawable.emoticon_neutral, 0);
                }else {
                    textViewNeededForGoal.setText("");
                    textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, R.drawable.emoticon_excited, 0);
                }
            } else{
                textViewNeededForGoal.setText("");
                textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, R.drawable.emoticon_neutral, 0);
            }
        }

        private void cartToStock(Item item) {

            if(item.getCart() > 0){

                ItemsController itemsController = new ItemsController();
                itemsController.cartToStock(context, item);
            }

        }

        private void decreaseItemCart(Item item) {

            if(item.getCart() == 0){
                Toast.makeText(context, "Nothing left to remove", Toast.LENGTH_SHORT).show();
            } else {

                ItemsController itemsController = new ItemsController();
                itemsController.decreaseItemCart(context, item);
            }
        }

        private void increaseCart(Item item) {

            ItemsController itemsController = new ItemsController();
            itemsController.increaseItemCart(context, item);

        }

        private  void increaseItemStock(Item item){

            ItemsController itemsController = new ItemsController();
            itemsController.increaseItemStock(context, item);

        }

        private void decreaseItemStock(Item item){

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

        private void assignTransitionNames(String itemID){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textViewItemName.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_NAME + itemID);
                textViewItemStock.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_STOCK + itemID);
                textViewItemIndependence.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_INDEPENDENCE+itemID);

            }
        }

        private void setTextViewItemIndependenceText(Item item){

            double independence = item.getIndependence();

            if(independence >= 365){
                textViewItemIndependence.setText( round(independence/365, 1) + " years");
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.emoticon_excited, 0, 0, 0);
            }
            else if(independence >= 30){
                textViewItemIndependence.setText( round(independence/30, 1) + " months");
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_insert_emoticon_black_24dp, 0, 0, 0);
            }
            else if(independence >= 7){
                textViewItemIndependence.setText( round(independence/7, 1) + " weeks");
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.emoticon_neutral, 0, 0, 0);
            } else{
                textViewItemIndependence.setText(Math.round(item.getConsumptionRate()*item.getStock()) + " days");
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_mood_bad_black_24dp, 0, 0, 0);
            }
        }

        private static Double round (double value, int precision) {
            int scale = (int) Math.pow(10, precision);
            return (double) Math.round(value * scale) / scale;
        }
    }
}
