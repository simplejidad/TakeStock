package com.santiagogil.takestock.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.santiagogil.takestock.R;

/**
 * Created by digitalhouse on 18/07/17.
 */

public class DialogAddItem extends DialogFragment {

    private AddItemDialogCommunicator communicator;
    private Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText input = new EditText(getActivity());
        input.setSingleLine(true);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(true);
        input.setHint("Item Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        builder.setView(input);

        //InputMethodManager keyboard = (InputMethodManager) builder.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //keyboard.showSoftInput(input, InputMethodManager.SHOW_FORCED);


        builder.setMessage("Adding a New Item:")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(input.getText().length() == 0){
                            Toast.makeText(context, "No Item has been added", Toast.LENGTH_SHORT).show();
                        } else{
                            communicator.addNewItem(input.getText().toString());
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public interface AddItemDialogCommunicator{
        void addNewItem(String itemName);

    }

    public void setCommunicator(AddItemDialogCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
