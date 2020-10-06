package com.haggle.forum;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.haggle.forum.Utilty.UpdateHelper;

import java.util.HashMap;
import java.util.Map;

public class Forums extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        Map<String,Object> defaultValue =new HashMap<>();

        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLED,false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION,"1.0.0");
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL,"kudupum");

        remoteConfig.setDefaults(defaultValue);
        remoteConfig.fetch(1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.activateFetched();

                        }
                    }
                });

    }
}
