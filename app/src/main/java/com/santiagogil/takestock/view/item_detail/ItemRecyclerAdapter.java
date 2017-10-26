package com.santiagogil.takestock.view.item_detail;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        private LinearLayout cardLayout;

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
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_view_item_linear_layout);
            this.context = context;
            this.onItemModifiedListener = onItemModifiedListener;
            this.itemView = itemView;
        }

        public void loadItem(final Item item){

            setTextsForLayout(item);
            setOnClickListeners(item);
            setDrawablesForButtons(item);
            setCardBackgroundColor(item);

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
                    increaseCart(item);
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
                    cartToStock(item);
                    onItemModifiedListener.onClick(itemView);
                }
            });

        }

        private void setTextsForLayout(Item item){

            textViewItemName.setText(item.getName());
            textViewItemStock.setText(item.getStock().toString());
            setTextViewItemIndependenceText(item);
            setTextViewNeededForGoal(item);
            textViewItemPrice.setText("" + item.getPrice());
            buttonStockAdd.setText("+" + item.getMinimumPurchaceQuantity());
            buttonCartToStock.setText("<" + item.getCart());

        }

        private void setDrawablesForButtons(Item item){
            if(item.getCart() == 0) {

                buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_black_24dp, 0, R.drawable.ic_shopping_cart_empty , 0);
            } else {

                buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_black_24dp, 0, R.drawable.ic_shopping_cart_black_24dp, 0);
            }
        }

        private void setTextViewNeededForGoal(Item item) {

            if(item.getConsumptionRate() > 0){

                textViewNeededForGoal.setText(item.getRoundedConsumptionRate());
                textViewNeededForGoal.setCompoundDrawablesRelativeWithIntrinsicBounds(0 , 0, item.getNextIndependenceEmoticon(), 0 );

            }
        }

        private void cartToStock(Item item) {

            if(item.getCart() > 0){

                ItemsController itemsController = new ItemsController();
                itemsController.cartToStock(context, item);
            } else {
                Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show();
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

                textViewItemIndependence.setText( item.getRoundedIndependence());
                textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        item.getIndependenceEmoticon(), 0, 0, 0);

        }

        public int getIntFromColor(int Red, int Green, int Blue){
            Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
            Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
            Blue = Blue & 0x000000FF; //Mask out anything not blue.

            return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
        }

        private void setCardBackgroundColor(Item item){

            Integer itemIndependence = 120 - item.getIndependence()/255*30;

            if(item.getIndependence() == 0){
                cardLayout.setBackgroundColor(getIntFromColor(255, 100, 100));
            } else {
                cardLayout.setBackgroundColor(getIntFromColor(itemIndependence, 255, itemIndependence));
            }

        }



    }
}
