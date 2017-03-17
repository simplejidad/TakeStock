package com.santiagogil.takestock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.DateHelper;

import java.util.Date;
import java.util.List;

public class ConsumptionRecyclerAdapter extends RecyclerView.Adapter {

    private List<Consumption> consumptionList;
    private Context context;


    public ConsumptionRecyclerAdapter(Context context) {
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_consumption, parent, false);
        return new ConsumptionViewHolder(view);
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

    static class ConsumptionViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewConsumptionDate;
        private DateHelper dateHelper;

        public ConsumptionViewHolder(View consumptionView) {
            super(consumptionView);
            textViewConsumptionDate = (TextView) consumptionView.findViewById(R.id.textViewConsumptionDate);
            dateHelper = new DateHelper();
        }

        public void loadConsumption(Consumption consumption){

            textViewConsumptionDate.setText(dateHelper.getFormatedDayFromMiliseconds(consumption.getDate()));

        }
    }
}
