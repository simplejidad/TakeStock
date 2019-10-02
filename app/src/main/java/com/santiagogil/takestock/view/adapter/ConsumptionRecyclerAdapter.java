package com.santiagogil.takestock.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.DateHelper;

import java.util.List;

public class ConsumptionRecyclerAdapter extends RecyclerView.Adapter {

    private List<Consumption> consumptionList;
    private Context context;
    private RecyclerConsumptionsFragmentCommunicator recyclerConsumptionsFragmentCommunicator;


    public ConsumptionRecyclerAdapter(Context context,
                                      RecyclerConsumptionsFragmentCommunicator recyclerConsumptionsFragmentCommunicator) {
        this.context = context;
        this.recyclerConsumptionsFragmentCommunicator = recyclerConsumptionsFragmentCommunicator;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_consumption, parent, false);
        return new ConsumptionViewHolder(context, view, recyclerConsumptionsFragmentCommunicator);
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
        private ImageButton buttonDelete;
        private Consumption consumption;

        public ConsumptionViewHolder(
                    final Context context,
                    View consumptionView,
                    final RecyclerConsumptionsFragmentCommunicator recyclerConsumptionsFragmentCommunicator
                ) {
            super(consumptionView);
            textViewConsumptionDate = (TextView) consumptionView.findViewById(R.id.textViewConsumptionDate);
            dateHelper = new DateHelper();
            buttonDelete = (ImageButton) consumptionView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConsumptionsController consumptionsController = new ConsumptionsController();
                    consumptionsController.deleteConsumption(context, consumption);
                    recyclerConsumptionsFragmentCommunicator.onConsumptionsUpdated();

                }
            });
        }

        public void loadConsumption(Consumption consumption){
            this.consumption = consumption;
            textViewConsumptionDate.setText(dateHelper.getFormatedDayFromMiliseconds(consumption.getDate()));
        }
    }

    public interface RecyclerConsumptionsFragmentCommunicator {
        void onConsumptionsUpdated();
    }

}
