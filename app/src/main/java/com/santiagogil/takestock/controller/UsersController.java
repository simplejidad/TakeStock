package com.santiagogil.takestock.controller;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.model.daos.UsersDAO;
import com.santiagogil.takestock.model.daos.UsersFirebaseDAO;
import com.santiagogil.takestock.model.pojos.User;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class UsersController {

    public void createUserWithEmailAndPassword(Context context, final ResultListener<Void> listenerFromActivity, String name, String email, String password, String image){

        UsersDAO usersDAO = new UsersDAO(context);
        usersDAO.createUserWithEmailAndPassword(new ResultListener<Void>(){
            @Override
            public void finish(Void result) {
                listenerFromActivity.finish(result);
            }
        }, name, email, password, image);
    }

    public void getAllUsersFromFirebase(final ResultListener<List<User>> listenerFromView){

        UsersFirebaseDAO usersFirebaseDAO = new UsersFirebaseDAO();
        usersFirebaseDAO.getAllUsers(new ResultListener<List<User>>() {
            @Override
            public void finish(List<User> result) {
                listenerFromView.finish(result);
            }
        });

    }

    public DatabaseReference getUserDB(User user){

        UsersFirebaseDAO usersFirebaseDAO = new UsersFirebaseDAO();
        return usersFirebaseDAO.getUserDB(user);

    }
}
