package com.santiagogil.takestock.controller;

import android.content.Context;

import com.santiagogil.takestock.model.daos.DatabaseHelper;
import com.santiagogil.takestock.model.daos.UsersDAO;
import com.santiagogil.takestock.util.ResultListener;

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
}
