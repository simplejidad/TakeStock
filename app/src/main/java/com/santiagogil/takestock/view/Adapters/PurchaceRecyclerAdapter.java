package com.santiagogil.takestock.view.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.PurchacesController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Purchace;
import com.santiagogil.takestock.util.DateHelper;

import java.util.List;

public class PurchaceRecyclerAdapter extends RecyclerView.Adapter {

    private List<Purchace> purchaceList;
    private Context context;
    private RecyclerPurchacesFragmentCommunicator recyclerPurchacesFragmentCommunicator;

    public PurchaceRecyclerAdapter(Context context, RecyclerPurchacesFragmentCommunicator recyclerPurchacesFragmentCommunicator) {
        this.context = context;
        this.recyclerPurchacesFragmentCommunicator = recyclerPurchacesFragmentCommunicator;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_consumption, parent, false);
        return new PurchaceViewHolder(context, view, recyclerPurchacesFragmentCommunicator);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Purchace purchace = purchaceList.get(position);
        PurchaceViewHolder purchaceViewHolder = (PurchaceViewHolder) holder;
        purchaceViewHolder.loadPurchace(purchace);
    }

    @Override
    public int getItemCount() {
        return purchaceList.size();
    }

    public void setConsumptionList(List<Purchace> purchaceList) {
        this.purchaceList = purchaceList;
    }

    public void setPurchacesList(List<Purchace> purchaceList) {

        this.purchaceList = purchaceList;
    }

    static class PurchaceViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewPurchacenDate;
        private DateHelper dateHelper;
        private ImageButton buttonDelete;
        private Purchace purchace;

        public PurchaceViewHolder(final Context context, View purchaceView, final RecyclerPurchacesFragmentCommunicator recyclerPurchacesFragmentCommunicator ) {
            super(purchaceView);
            textViewPurchacenDate = (TextView) purchaceView.findViewById(R.id.textViewConsumptionDate);
            dateHelper = new DateHelper();
            buttonDelete = (ImageButton) purchaceView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PurchacesController purchacesController = new PurchacesController();
                    purchacesController.deletePurchace(context, purchace);
                    recyclerPurchacesFragmentCommunicator.onPurchacesUpdated();

                }
            });

        }

        public void loadPurchace(Purchace purchace){
            this.purchace = purchace;
            textViewPurchacenDate.setText(dateHelper.getFormatedDayFromMiliseconds(purchace.getDate()));

        }
    }

    public interface RecyclerPurchacesFragmentCommunicator{

        void onPurchacesUpdated();

    }

}
