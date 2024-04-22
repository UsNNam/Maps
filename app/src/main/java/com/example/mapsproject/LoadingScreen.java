package com.example.mapsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mapsproject.Authentication.LoginActivity;

public class LoadingScreen extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        GlobalVariable.userName = "";
        session = new SessionManager(getApplicationContext());
//        session.Clear();
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
            GlobalVariable.userName = session.getUsername();
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(LoadingScreen.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}