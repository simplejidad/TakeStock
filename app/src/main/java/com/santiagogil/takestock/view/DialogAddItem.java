package com.santiagogil.takestock.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import com.santiagogil.takestock.R;

/**
 * Created by digitalhouse on 18/07/17.
 */

public class DialogAddItem extends DialogFragment {

    private AddItemDialogCommunicator communicator;

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
        builder.setView(input);

        builder.setMessage("Add a New Item")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String itemName = input.getText().toString();
                        if(itemName.equals("")){
                            dialog.cancel();
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
}
