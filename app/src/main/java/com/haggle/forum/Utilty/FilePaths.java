package com.haggle.forum.Utilty;

import android.os.Environment;


public class FilePaths {
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String CHAT = "/Forums/Chat/";
    public String DP = ROOT_DIR + "/Forums/Dp/";
    public String DOWNLOAD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    public String PICTURES  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    public String DOCUMENTS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();



    public String FIREBASE_STORY_STORAGE = "stories/users";
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
