package com.example.user.nicadepartments.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by USER on 04/03/2018.
 */

public class Api {

    private final static String URL = "https://blooming-harbor-80414.herokuapp.com/v1/";

    public static ApiInterface instance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }


}
