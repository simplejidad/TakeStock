package com.santiagogil.takestock.view.onboarding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.MainActivityCommunicator;

import java.util.List;

public class RegisterFragment extends Fragment {

    private EditText editTextNameField;
    private EditText editTextEmailField;
    private EditText editTextPasswordField;
    private EditText editTextConfirmPasswordField;

    private Button buttonRegister;

    private FirebaseAuth fAuth;
    private DatabaseReference firebase;

    private ProgressDialog progressDialog;

    public static final String EMAIL = "email";


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextNameField = (EditText) view.findViewById(R.id.name);
        editTextEmailField = (EditText) view.findViewById(R.id.email);
        editTextPasswordField = (EditText) view.findViewById(R.id.password);
        editTextConfirmPasswordField = (EditText) view.findViewById(R.id.confirmPassword);
        buttonRegister = (Button) view.findViewById(R.id.email_register_button);

        fAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                startRegister();

            }
        });

        Bundle bundle = getArguments();

        if(bundle != null){
            editTextEmailField.setText(bundle.getString(EMAIL));
        }

        return view;
    }
    private void startRegister() {

        final String name = editTextNameField.getText().toString().trim();
        String email = editTextEmailField.getText().toString().trim();
        String password = editTextPasswordField.getText().toString().trim();
        String confirmedPassword = editTextConfirmPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmedPassword)){

            if (password.equals(confirmedPassword)){

                if(password.length()<6){

                    Toast.makeText(getContext(), getString(R.string.error_invalid_password) , Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setMessage("Signing Up...");
                    progressDialog.show();

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseHelper firebaseHelper = new FirebaseHelper();

                                firebaseHelper.getFirebaseDatabase().getReference().child(DatabaseHelper.TABLEUSERS).
                                        child(firebaseHelper.getCurrentUserID()).
                                        child(DatabaseHelper.IMAGE).setValue("default");
                                firebaseHelper.getFirebaseDatabase().getReference().child(DatabaseHelper.TABLEUSERS)
                                        .child(firebaseHelper.getCurrentUserID())
                                        .child(DatabaseHelper.NAME).setValue(name);

                                ItemsController itemsController = new ItemsController();
                                itemsController.updateItemsDatabase(getContext(), new ResultListener<List<Item>>(){
                                    @Override
                                    public void finish(List<Item> result) {
                                        Toast.makeText(getContext(), "Items Updated", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                        Intent mainIntent = new Intent(getContext(), MainActivityCommunicator.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mainIntent);
                                    }
                                });

                            } else {

                                Toast.makeText(getContext(), "Register Problem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
            else{
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), "Fields are Empty", Toast.LENGTH_SHORT).show();
        }
    }

}
