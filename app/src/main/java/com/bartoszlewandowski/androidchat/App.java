package com.bartoszlewandowski.androidchat;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Bartosz Lewandowski on 08.03.2019.
 */
public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("oIoseq2wXWnAD8NmFuLIJaB47KEj0Aez6s8JrPZJ")
                .clientKey("KvkcxgqUm2eFsQqV3BaNgln9mXQvU4NRpTR66IIR")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
