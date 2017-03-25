package com.santiagogil.takestock.view.item_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.DateHelper;

import java.util.List;

public class ConsumptionRecyclerAdapter extends RecyclerView.Adapter {

    private List<Consumption> consumptionList;
    private Context context;
    private FragmentItemDetail.OnConsumptionDeletedListener onConsumptionDeletedListener;


    public ConsumptionRecyclerAdapter(Context context,
                                      FragmentItemDetail.OnConsumptionDeletedListener onConsumptionDeletedListener) {
        this.context = context;
        this.onConsumptionDeletedListener = onConsumptionDeletedListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_consumption, parent, false);
        return new ConsumptionViewHolder(view, onConsumptionDeletedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Consumption consumption = consumptionList.get(position);
        ConsumptionViewHolder consumptionViewHolder = (ConsumptionViewHolder) holder;
        consumptionViewHolder.loadConsumption(consumption);
    }

    @Override
    public int getItemCount() {
        return consumptionList.size();
    }

    public void setConsumptionList(List<Consumption> consumptionList) {
        this.consumptionList = consumptionList;
    }

    public Consumption getConsumptionAtPosition(Integer position){return consumptionList.get(position);}

    static class ConsumptionViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewConsumptionDate;
        private DateHelper dateHelper;
        private Button buttonDelete;
        private Consumption consumption;

        public ConsumptionViewHolder(View consumptionView,
                                     final FragmentItemDetail.OnConsumptionDeletedListener onConsumptionDeletedListener) {
            super(consumptionView);
            textViewConsumptionDate = (TextView) consumptionView.findViewById(R.id.textViewConsumptionDate);
            dateHelper = new DateHelper();
            buttonDelete = (Button) consumptionView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onConsumptionDeletedListener.onConsumptionDeleted(consumption);
                }
            });
        }

        public void loadConsumption(Consumption consumption){
            this.consumption = consumption;
            textViewConsumptionDate.setText(dateHelper.getFormatedDayFromMiliseconds(consumption.getDate()));

        }
    }
}
