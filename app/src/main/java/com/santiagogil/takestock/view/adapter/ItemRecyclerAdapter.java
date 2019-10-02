package com.santiagogil.takestock.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

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
        return new ItemViewHolder(view, context, onItemModifiedListener, onItemTouchedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.loadItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private View.OnClickListener onItemModifiedListener;
        private View itemView;
        private ItemsController itemsController = new ItemsController();

        @BindView(R.id.text_view_item_name) TextView textViewItemName;
        @BindView(R.id.text_view_item_stock) TextView textViewItemStock;
        @BindView(R.id.text_view_item_independence) TextView textViewItemIndependence;
        @BindView (R.id.text_view_item_price) TextView  textViewItemPrice;
        @BindView(R.id.text_view_needed_for_goal) TextView textViewNeededForGoal;
        @BindView(R.id.buttonSubtract) ImageView buttonStockSubtract;
        @BindView(R.id.button_cart_subtract) ImageView buttonCartSubtract;
        @BindView(R.id.buttonAdd) TextView buttonStockAdd;
        @BindView(R.id.button_cart_add) ImageView buttonCartAdd;
        @BindView(R.id.button_cart_to_stock) TextView buttonCartToStock;
        @BindView(R.id.vertical_bar) FrameLayout verticalColorBar;

        public ItemViewHolder(View itemView, Context context, View.OnClickListener onItemModifiedListener, View.OnClickListener onItemTouchedListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(onItemTouchedListener);

            this.context = context;
            this.onItemModifiedListener = onItemModifiedListener;
            this.itemView = itemView;
        }

        public void loadItem(final Item item){
            setTextsForLayout(item);
            setOnClickListeners(item);
            setDrawablesForButtons(item);
            setCardBackgroundColor(verticalColorBar, item.getIndependence());
        }

        private void setOnClickListeners(final Item item){

            buttonStockAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseItemStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });

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
                    increaseAmmountInCart(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });

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
                    moveFromCartToStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });

        }

        private void setTextsForLayout(Item item){

            textViewItemName.setText(item.getName());
            textViewItemStock.setText(item.getStock().toString());
            setTextViewItemIndependenceText(item);
            setTextViewNeededForGoal(item);
            textViewItemPrice.setText("$" + item.getPrice());
            buttonStockAdd.setText(item.getMinimumPurchaceQuantity().toString());
            if(item.getMinimumPurchaceQuantity() > 9){
                buttonStockAdd.setPaddingRelative(12, 0, 0, 0);
            }
            buttonCartToStock.setText(item.getUnitsInCart().toString());

        }

        private void setDrawablesForButtons(Item item){

            if(item.getStock() == 0) {
                buttonStockSubtract.setImageResource(R.drawable.ic_home_minus_disabled);
            } else{
                buttonStockSubtract.setImageResource(R.drawable.ic_home_minus_enabled);
            }
            if(item.getUnitsInCart() == 0) {

                buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_to, 0, 0 , 0);
                buttonCartToStock.setVisibility(View.GONE);
                buttonCartSubtract.setImageResource(R.drawable.ic_cart_substract_disabled);
            } else {
                buttonCartToStock.setVisibility(VISIBLE);
                buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_to, 0, R.drawable.ic_shopping_cart_black_24dp, 0);
            }
        }

        private void setTextViewNeededForGoal(Item item) {
            if(item.getConsumptionRate() > 0)
                textViewNeededForGoal.setText(item.getRoundedConsumptionRate());
        }

        private void moveFromCartToStock(Item item) {
            if(itemIsInCart(item))
                itemsController.moveFromCartToStock(context, item);
            else
                Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show();
        }

        private boolean itemIsInCart(Item item) {
            return item.getUnitsInCart() > 0;
        }

        private void decreaseItemCart(Item item) {
            if(isEmptyItemCart(item))
                Toast.makeText(context, "Nothing left to remove", Toast.LENGTH_SHORT).show();
            else
                itemsController.decreaseItemCart(context, item);
        }

        private boolean isEmptyItemCart(Item item) {
            return item.getUnitsInCart() == 0;
        }

        private void increaseAmmountInCart(Item item) {
            itemsController.increaseItemCart(context, item);
        }

        private  void increaseItemStock(Item item){
            itemsController.increaseItemStock(context, item);
        }

        private void decreaseItemStock(Item item){

            if(item.getStock() == 0)
                Toast.makeText(context, "Nothing left to consume", Toast.LENGTH_SHORT).show();
            else {
                itemsController.decreaseItemStock(context, item);
                ConsumptionsController consumptionsController = new ConsumptionsController();
                consumptionsController.addConsumptionToDatabases(context, item.getID());
                itemsController.updateItemConsumptionRate(context, item.getID());
            }
        }

        private void setTextViewItemIndependenceText(Item item){

                textViewItemIndependence.setText( item.getRoundedIndependence());
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        item.getIndependenceEmoticon(), 0, 0, 0);

        }

        private void setCardBackgroundColor(FrameLayout verticalColorBar, Integer independence){

            if(independence == 0){
                verticalColorBar.setBackgroundColor(ContextCompat.getColor(context, R.color.accent));
            } else if(independence < 60){
                verticalColorBar.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            } else if(independence >=60){
                verticalColorBar.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            }
        }
    }
}
