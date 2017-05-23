package com.santiagogil.takestock.view.onboarding;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private LoginFragment.OnboardingActivityCommunicator onboardingActivityCommunicator;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;


    public static final String EMAIL = "email";

    public void setOnboardingActivityCommunicator(LoginFragment.OnboardingActivityCommunicator onboardingActivityCommunicator) {
        this.onboardingActivityCommunicator = onboardingActivityCommunicator;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextNameField = (EditText) view.findViewById(R.id.name);
        editTextEmailField = (EditText) view.findViewById(R.id.email);
        editTextPasswordField = (EditText) view.findViewById(R.id.password);
        editTextConfirmPasswordField = (EditText) view.findViewById(R.id.confirmPassword);
        buttonRegister = (Button) view.findViewById(R.id.email_register_button);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.text_input_layout_password);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.text_input_layout_email);
        textInputLayoutConfirmPassword = (TextInputLayout) view.findViewById(R.id.text_input_layout_confirm_password);

        fAuth = FirebaseAuth.getInstance();

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

        editTextEmailField.addTextChangedListener(new TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                      }

                                                      @Override
                                                      public void afterTextChanged(Editable editable) {

                                                          if(!isValidEmail((CharSequence) editable)){
                                                              textInputLayoutEmail.setError("Enter a valid email address");
                                                          } else{
                                                              textInputLayoutEmail.setError(null);
                                                          }

                                                      }
                                                  }


        );

        editTextPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!isPasswordValid((CharSequence) editable)){
                    textInputLayoutPassword.setError("Password must be at least 6 characters long");
                } else{
                    textInputLayoutPassword.setError(null);
                }

            }
        });

        editTextConfirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!passwordsMatch((CharSequence) editable)) {
                    editTextConfirmPasswordField.setError("Passwords must match");
                } else {
                    editTextConfirmPasswordField.setError(null);
                }
            }
        });

        return view;
    }

    private boolean passwordsMatch(CharSequence target) {

        return editTextPasswordField.getText().equals(target);
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

                                        onboardingActivityCommunicator.startMainActivity();

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

    public interface OnboardingActivityCommunicator {

        void startMainActivity();

    }

    private boolean isPasswordValid(CharSequence target) {

        return target.length()>5;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

/*    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(), "RegisterFragment Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getContext(), "RegisterFragment Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getContext(), "RegisterFragment Destroyed", Toast.LENGTH_SHORT).show();
    }*/

}
