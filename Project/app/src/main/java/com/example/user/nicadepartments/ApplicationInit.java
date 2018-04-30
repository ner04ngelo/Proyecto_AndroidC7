package com.example.user.nicadepartments;

import com.tumblr.remember.Remember;

import io.realm.Realm;

/**
 * Created by USER on 26/4/2018.
 */

public class ApplicationInit extends android.app.Application {
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Remember.init(getApplicationContext(), "nicadepartments.sync");
    }
}
