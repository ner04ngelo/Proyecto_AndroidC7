package com.example.user.nicadepartments.Views;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.user.nicadepartments.MainActivity;
import com.example.user.nicadepartments.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundColor(Color.parseColor("#FFFFFF"))
                .withLogo(R.drawable.logo)
                .withAfterLogoText("Bienvenido!!")
                .withFooterText("Copyright 2018")
                .withBeforeLogoText("Chamos Devolopers");


        config.getAfterLogoTextView().setTextColor(Color.BLACK);
        config.getAfterLogoTextView().setTextSize(18);
        config.getFooterTextView().setTextColor(android.graphics.Color.BLACK);
        config.getBeforeLogoTextView().setTextColor(android.graphics.Color.BLACK);
        config.getBeforeLogoTextView().setTextSize(18);
        /*config.getLogo().setMaxHeight(400);
        config.getLogo().setMaxWidth(400);*/

        View view = config.create();

        setContentView(view);

}
}
