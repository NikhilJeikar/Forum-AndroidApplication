package com.haggle.forum.Utilty;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class UpdateHelper {
    public static String KEY_UPDATE_ENABLED = "isUpdate";
    public static String KEY_UPDATE_VERSION = "Version";
    public static String KEY_UPDATE_URL = "Update_Url";

    public interface OnUpdateCheckListener{
        void onUpdateCheckListener(String urlApp);
    }

    public static  Builder with(Context context){
        return new Builder(context);
    }

    private OnUpdateCheckListener onUpdateCheckListener;
    private Context context;

    public UpdateHelper(OnUpdateCheckListener onUpdateCheckListener, Context context) {
        this.onUpdateCheckListener = onUpdateCheckListener;
        this.context = context;
    }
    public void Check(){
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        if(remoteConfig.getBoolean(KEY_UPDATE_ENABLED)){
            String CurrentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
            String AppVersion = getAppVersion(context);
            String UpdateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            if(!TextUtils.equals(CurrentVersion,AppVersion) && onUpdateCheckListener != null){
                onUpdateCheckListener.onUpdateCheckListener(UpdateUrl);
            }
        }
    }

    private String getAppVersion(Context context) {
        String Result = "";
        try {
            Result = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            Result = Result.replaceAll("[a-zA-Z]|-","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Result;
    }

    public static class Builder{
        private Context context;
        private OnUpdateCheckListener onUpdateCheckListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateCheck(OnUpdateCheckListener onUpdateCheckListener){
            this.onUpdateCheckListener = onUpdateCheckListener;
            return this;
        }

        public UpdateHelper build(){
            return new UpdateHelper(onUpdateCheckListener,context);
        }

        public UpdateHelper check(){
            UpdateHelper updateHelper = build();
            updateHelper.Check();

            return updateHelper;
        }
    }
}
